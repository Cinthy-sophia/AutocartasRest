package com.cinthyasophia.autocartasrest.services;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.google.gson.Gson;

import CRUDs.CRUDPartidas;
import Clases.Cartas;
import Clases.HibernateUtil;
import Clases.Jugadas;
import Clases.Jugadores;
import Clases.Partidas;


@Path("/autocartas")
public class Main extends ResourceConfig {
	public enum caracteristicas{ MOTOR,POTENCIA,VELOCIDAD_MAXIMA,CILINDRADA,REVOLUCIONES_POR_MINUTO,CONSUMO}
	private final int CANTIDAD_CARTAS = 6;
	private final String NOMBRE = "CPU";
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	private ArrayList<Cartas> cartasCPU;
	private ArrayList<Partidas> partidas;
	private ArrayList<Jugadores> jugadores;
	private ArrayList<Jugadas> jugadas;
	
	
	/**
	 * Valida que el usuario ingresado es correcto
	 * @param nick
	 * @param password
	 * @return
	 */
	@POST
	@Path("sesion_nueva")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response validarJugador(@FormParam("nick") String nick, @FormParam("password") String password) {
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			jugadores =(ArrayList<Jugadores>) q.list();			
		}
		
		String nickJugador = g.fromJson(nick, String.class); 
		String passwordJugador = g.fromJson(password, String.class); 
		
		for (Jugadores j : jugadores) {
			if(j.getNick().equalsIgnoreCase(nickJugador)) {
				UID id = new UID();
				j.setSesionActual(id.toString());
				session.save(j);
				transaction.commit();
				
				return Response.status(Response.Status.OK).entity(g.toJson(j.getSesionActual())).build();
			}
		}
		
		//Si el usuario indicado no existe le envia null, asi el cliente indica al usuario que debe crear uno nuevo
		
		return Response.status(Response.Status.OK).entity(g.toJson(null)).build();

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
		Gson g = new Gson();
		Random rnd = new Random();
		ArrayList<Cartas> baraja = new ArrayList<>();
		
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Cartas");
		
		if(q.list().size()!=0) {
			baraja =(ArrayList<Cartas>) q.list();			
		}
		cartasCPU = new ArrayList<>();
		
		int al;
		
		for (int i = 0; i < CANTIDAD_CARTAS; i++) {
			al= rnd.nextInt(CANTIDAD_CARTAS - 1 +1 ) + 1;
			cartasCPU.add(baraja.get(al)); 
			baraja.remove(al);
		}
		
		ArrayList<Cartas> cartasUsuario = baraja;
		return Response.status(Response.Status.OK).entity(g.toJson(cartasUsuario)).build();
	}	
	/**
	 * Inicia una nueva partida para el jugador, si encuentra una partida no finalizada la elimina
	 * e inicia una nueva
	 * @param idSesion
	 * @return
	 */
	@POST
	@Path("partidas")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response nuevaPartida(@FormParam("idSesion")String idSesion ) {
		CRUDPartidas cp = new CRUDPartidas();
		Gson g = new Gson();
		//String sesion = g.fromJson(idSesion, String.class);
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Partidas");
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
		
		ArrayList<Partidas> it;	

		if(partidas!=null) {
			it = partidas;
			for (Partidas p : it) {
				if(p.getSesion().equalsIgnoreCase(idSesion) || !p.isFinalizada()) {
					//Si ya hay una partida iniciada
					if(cp.borrarPartidas(g.toJson(p.getId())) != null) {
						partidas.remove(p);						
						
					}
					
				}
			}
			
		}else {
			partidas = new ArrayList<>();

		}
		Partidas pN = new Partidas();
		pN.setSesion(idSesion);
		session.save(pN);
		transaction.commit();
		
		partidas.add(pN);
		//Devuelvo el objeto, y en el cliente verifico que esté finalizada o no, para reiniciarla o no
		return Response.status(Response.Status.OK).entity(g.toJson(pN)).build();

	}
	
	/**
	 * Realiza el sorteo de quien debe empezar primero
	 * @return
	 */
	@GET
	@Path("sorteo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response sorteoTurno() {
		Random rnd = new Random();
		Gson g = new Gson();
		int al;
		al= rnd.nextInt(1 - 0 +1 ) + 1;
		//si devuelve 0 inicia el CPU
		//Si devuelve 1 inicia el jugador
		return Response.status(Response.Status.OK).entity(g.toJson(al)).build(); 	
		
	}
	/**
	 * Se encarga de obtener la mejor carta para jugar en funcion de la caracteristica que recibe
	 * @param caracteristica
	 * @return
	 */
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
	
	/**
	 * En caso de que al servidor le toque comenzar, el cliente le avisa al servidor que ya puede iniciar la jugada, 
	 * y este le envía la primera jugada
	 * @param idSesion
	 * @param idPartida
	 * @return
	 */
	@POST
	@Path("game_on")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response ready(@FormParam("idSesion") String idSesion) {
		int mano=0;
		String caract;
		String sesion;
		int al;
		Random rnd = new Random();
		Gson g = new Gson();
		
		al= rnd.nextInt(CANTIDAD_CARTAS - 1 +1 ) + 1;
		
		//sesion = g.fromJson(idSesion, String.class);
		
		Jugadas jugadaEnvia = new Jugadas();

		caract = caracteristicas.values()[al-1].toString().toLowerCase();
		
		mano++;
		
		Cartas cartaEnvia = cartasCPU.remove(cartasCPU.indexOf(buscarPorCaracteristica(caract.toLowerCase())));
		jugadaEnvia = new Jugadas(cartaEnvia, idSesion,caracteristicas.values()[al].toString().toLowerCase(),NOMBRE,mano);
		
		return Response.status(Response.Status.OK).entity(g.toJson(jugadaEnvia)).build();
		
	}
	
	/**
	 * Se encarga de enviar cartas al cliente hasta que el array se vacía
	 * @param jugada
	 * @param idSesion
	 * @return
	 */
	@POST
	@Path("jugada")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response jugarCarta( String jugada, @FormParam("idSesion") String idSesion) {
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
		
		Cartas cartaEnvia = cartasCPU.remove(cartasCPU.indexOf(buscarPorCaracteristica(caract.toLowerCase())));
		jugadaEnvia = new Jugadas(cartaEnvia, sesion,caracteristicas.values()[al].toString(),NOMBRE,mano);
		
		return Response.status(Response.Status.OK).entity(g.toJson(jugadaEnvia)).build();
		
	}
	
	

}