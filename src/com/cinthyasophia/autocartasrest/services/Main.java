package com.cinthyasophia.autocartasrest.services;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.google.gson.Gson;

import Clases.Cartas;
import Clases.HibernateUtil;
import Clases.Jugadas;
import Clases.Jugadores;
import Clases.Partidas;


@Path("/autocartas")
public class Main extends ResourceConfig {
	public enum caracteristicas{ MOTOR,POTENCIA,VELOCIDAD_MAXIMA,CILINDRADA,REVOLUCIONES_POR_MINUTO,CONSUMO};
	private final int CANTIDAD_CARTAS = 6;
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	private ArrayList<Cartas> cartasCPU;
	private ArrayList<Partidas> partidas;
	private ArrayList<Jugadores> jugadores;
	private ArrayList<Jugadas> jugadas;
	private Partidas partidaActual;
	private int manoActual;
	
	
	@POST
	@Path("sesion_nueva")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response validarJugador(@FormParam("jugador") String jugador) {
		Gson g = new Gson();
		jugadores = (ArrayList<Jugadores>) obtenerTablas("jugadores").getEntity();
		Jugadores jug = g.fromJson(jugador, Jugadores.class); 
		
		for (Jugadores j : jugadores) {
			if(j.getNick().equalsIgnoreCase(jug.getNick())) {
				UID id = new UID();
				j.setSesionActual(id.toString());
				
				return Response.status(200).entity(g.toJson(j.getSesionActual())).build();
			}
		}
		
		//Si el usuario indicado no existe le envia null, asi el cliente indica al usuario que debe crear uno nuevo
		
		return Response.status(200).entity(null).build();

	}
	
	/**
	 * Obtiene las cartas de la base de datos y las reparte tanto para el CPU como para
	 * el jugador, luego de ello, las envia en formato Json.
	 * @return Response
	 */
	@GET
	@Path("baraja_cartas")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerYRepartirCartas() {
		Random rnd = new Random();
		ArrayList<Cartas> baraja = (ArrayList<Cartas>) obtenerTablas("cartas").getEntity();
		cartasCPU = new ArrayList<>();
		
		int al;
		
		for (int i = 0; i < CANTIDAD_CARTAS; i++) {
			al= rnd.nextInt(CANTIDAD_CARTAS - 1 +1 ) + 1;
			cartasCPU.add(baraja.get(al)); 
			baraja.remove(al);
		}
		
		ArrayList<Cartas> cartasUsuario = baraja;
		Gson g = new Gson();
		return Response.status(200).entity(g.toJson(cartasUsuario)).build();
	}

	@POST
	@Path("jugada")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response jugarCarta(@FormParam("jugada") String jugada, @FormParam("idSesion") String idSesion) {
		String caract;
		int mano;
		String sesion;
		int al;
		
		Random rnd = new Random();
		Gson g = new Gson();
		Jugadas jugadaRecibe = g.fromJson(jugada, Jugadas.class);
		sesion = g.fromJson(idSesion, String.class);
		Jugadas jugadaEnvia = new Jugadas();

		caract = jugadaRecibe.getCaracteristica();
		mano = jugadaRecibe.getMano();
		
		mano++;
		al= rnd.nextInt(CANTIDAD_CARTAS - 1 +1 ) + 1;
		
		Cartas cartaEnvia = buscarPorCaracteristica(caract);
		jugadaEnvia = new Jugadas(cartaEnvia, idSesion,caracteristicas.values()[al].toString(),mano);
		
		return Response.status(200).entity(g.toJson(jugadaEnvia)).build();

	}
	public Cartas buscarPorCaracteristica(String caracteristica) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		Cartas elegida = null;
		for (int i = 0; i < cartasCPU.size(); i++) {
			switch (caracteristica) {
				case "motor":
					
					if(cartasCPU.get(i).getMotor() > max) {
						max  = cartasCPU.get(i).getMotor();
						elegida = cartasCPU.get(i);
					}
					
					break;
				case "potencia":
					if(cartasCPU.get(i).getPotencia() > max) {
						max  = cartasCPU.get(i).getPotencia();
						elegida = cartasCPU.get(i);
					}
					break;
				case "velocidad_maxima":
					if(cartasCPU.get(i).getVelocidadMaxima() > max) {
						max  = cartasCPU.get(i).getVelocidadMaxima();
						elegida = cartasCPU.get(i);
					}
					break;
				case "cilindrada":
					if(cartasCPU.get(i).getCilindrada() > max) {
						max  = cartasCPU.get(i).getCilindrada();
						elegida = cartasCPU.get(i);
					}
					break;
				case "revoluciones_por_minuto":
					if(cartasCPU.get(i).getRevolucionesPorMinuto() < min) {
						min  = cartasCPU.get(i).getRevolucionesPorMinuto();
						elegida = cartasCPU.get(i);
					}
					break;
				case "consumo":
					if(cartasCPU.get(i).getConsumo() < min) {
						min = (int) cartasCPU.get(i).getConsumo();
						elegida = cartasCPU.get(i);
					}
					break;
					
				default:
					elegida = null;
					break;
			}
			
		}
		if(elegida == null) {
			int al;
			Random rnd = new Random();
			al= rnd.nextInt(CANTIDAD_CARTAS - 1 +1 ) + 1;
			elegida = cartasCPU.get(al);
		}
		
		return elegida;
			
	}
	
	

	@GET
	@Path("{tabla}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTablas(@PathParam("tabla") String tabla) {
		ArrayList datos = null;
		switch (tabla) {
			case "cartas":
				datos = new ArrayList<Cartas>();
			case "partidas":
				datos = new ArrayList<Partidas>();
				break;
			case "jugadores":
				datos = new ArrayList<Jugadores>();
					break;
			case "jugadas":
				datos = new ArrayList<Jugadas>();
				break;
			
			default:
				break;
		}
		
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from "+StringUtils.capitalize(tabla));
		
		if(q.list().size()!=0) {
			datos =(ArrayList) q.list();			
		}
		Gson g = new Gson();
		return Response.status(200).entity(g.toJson(datos)).build();
		
	}
	
	/**
	 * Insertamos un nuevo jugador a la base de datos para que pueda ser utilizado en una partida.
	 * Recibe el jugador, y luego comprueba que no exista en la base de datos, para luego insertarlo.
	 * @param jugador
	 * @return
	 */
	
	@POST
	@Path("jugadores")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response nuevoJugador(@FormParam("jugador")String jugador) {
		Gson g = new Gson();
		ArrayList<Jugadores> jugadores = (ArrayList<Jugadores>) obtenerTablas("jugadores").getEntity();
		Jugadores jug = g.fromJson(jugador, Jugadores.class);
		
		for (Jugadores j : jugadores) {
			if(j.getNick().equalsIgnoreCase(jug.getNick())) {
				//Si lo encuentra devuelve null 
				return Response.status(200).entity(null).build();
				
			}
		}
		session.save(jugador);
		transaction.commit();
		
		return Response.status(200).entity(g.toJson(jugador)).build();

	}
	
	@POST
	@Path("partidas")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response nuevaPartida(@FormParam("idSesion")String idSesion ) {
		Gson g = new Gson();
		String sesion = g.fromJson(idSesion, String.class);
		ArrayList<Partidas> partidas = (ArrayList<Partidas>) obtenerTablas("partidas").getEntity();
		if(partidas!=null) {
			for (Partidas p : partidas) {
				if(p.getJugadores().getSesionActual().equalsIgnoreCase(sesion)) {
					return Response.status(200).entity(g.toJson(p.getId())).build();
					
				}
			}
			
			this.partidas = partidas; 
		}
		Partidas pN = new Partidas();
		pN.setSesion(sesion);
		
		session.save(pN);
		transaction.commit();
		
		this.partidas.add(pN);
		
		Response rCartas = obtenerYRepartirCartas();
		return rCartas;

	}
	
	
	@GET
	@Path("sorteo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response sorteoTurno() {
		Random rnd = new Random();
		Gson g = new Gson();
		int al;
		al= rnd.nextInt(0-1 +1 ) + 1;
		return Response.status(200).entity(g.toJson(al)).build(); 	

	}
	
	@POST
	@Path("partida_desde_cero")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response reiniciarPartida(@FormParam("idSesion")String idSesion, @FormParam("idPartida") String idPartida) {
		Gson g = new Gson();
		String sesion = g.fromJson(idSesion, String.class);
		int idP = g.fromJson(idPartida, Integer.class);
		
		for (Partidas p : partidas) {
			if(p.getId() == idP && p.getSesion().equalsIgnoreCase(sesion)) {
				partidas.remove(p);
				
				//Devuelve null para indicar que ya ha sido borrada
				return Response.status(200).entity(null).build();
				
			}
		}
		
		return Response.status(200).entity(g.toJson(idP)).build();

	}
	
	
	@POST
	@Path("{tabla}")
	public String insertarDatos(@PathParam("tabla") String tabla, String json) {
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from "+StringUtils.capitalize(tabla));
		Object datos = null;
		//Segun el dato pedido se insertará uno u otro
		switch (tabla) {
			
			case "partidas":
				datos = new ArrayList<Partidas>();
				break;
			case "jugadores":
				datos = new ArrayList<Jugadores>();
					break;
			case "jugadas":
				datos = new ArrayList<Jugadas>();
				break;
			
			default:
				break;
		}
		//TLineaEstacion l = new TLineaEstacion(codTLineaE, estaciones, lineas, Integer.parseInt(tfOrden.getText()));
		//session.save(l);
		transaction.commit();
		return "<!DOCTYPE html><html><body>Hola que pasa?</body></html>";
		
	}
	
	@PUT
	@Path("{tabla}")
	public String actualizarDatos(@PathParam("tabla") String tabla) {
		return "<!DOCTYPE html><html><body>Hola que pasa?</body></html>";
		
	}
	
	@DELETE
	@Path("{tabla}")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response borrarDatos(@PathParam("tabla") String tabla) {
		String message = "DELEEEETE!";
		
		System.out.println("DELEEETE!");
		return Response.status(200).entity(message).build();
	}
	

//	//Json desde Gson
//	@GET
//	@Path("/personas")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String JsonPersona() {
//		Persona p1 = new Persona("Yo","Mismo",33);
//		Persona p2 = new Persona("Tu","Mismo",34);
//		Persona p3 = new Persona("El","Mismo",35);
//		ArrayList<Persona> listP = new ArrayList<>();
//		listP.add(p1);
//		listP.add(p2);
//		listP.add(p3);
//	
//		Gson g = new Gson();
//		return g.toJson(listP);
//	}
//	//Postman
//	@POST
//	@Path("/persona")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void JsonPersonaPost(String persona) {
//	
//		Gson g = new Gson();
//		Persona p1 = new Persona();
//		p1=g.fromJson(persona,Persona.class);
//		System.out.println(p1.getNombre());
//		
//	
//	}
	//File
	//Response
//	@GET
//	@Path("respuesta")
//	public Response getOkResponse() {
//		String message = "Mensajito pa ti!";
//		return Response.status(200).entity(message).build();
//	}
//	//Response obj
//	@GET
//	@Path("respuestaObj")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response getOkResponseObj() {
//		Persona p = new Persona("Yo","Mismo",33);	
//		Gson g = new Gson();
//		return Response.status(200).entity(g.toJson(p)).build();
//	}
	//Response put
	@PUT
	@Path("respuesta")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response getOkResponsePut() {
		String message = "PUUUUUUT!";
		System.out.println("PUUUUUT!");
		return Response.status(200).entity(message).build();
	}
	@DELETE
	@Path("respuesta")
	//@Consumes(MediaType.APPLICATION_JSON)
	public Response getOkResponseDelete() {
		String message = "DELEEEETE!";
		
		System.out.println("DELEEETE!");
		return Response.status(200).entity(message).build();
	}
	
}