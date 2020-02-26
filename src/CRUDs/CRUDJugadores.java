package CRUDs;

import java.util.ArrayList;

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
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.google.gson.Gson;

import Clases.HibernateUtil;
import Clases.Jugadas;
import Clases.Jugadores;
import Clases.Partidas;

@Path("/jugadores")
public class CRUDJugadores extends ResourceConfig {
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	/**
	 * Muestra los jugadores que existen en la base de datos
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPartidas() {
		ArrayList<Jugadores> jugadores = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			jugadores =(ArrayList<Jugadores>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(jugadores)).build();
	}
	
	/**
	 * Insertamos un nuevo jugador a la base de datos para que pueda ser utilizado en una partida.
	 * Recibe el jugador, y luego comprueba que no exista en la base de datos, para luego insertarlo.
	 * @param jugador
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertarJugadores(@FormParam("nick") String nick, @FormParam("password") String password) {
		ArrayList<Jugadores> jugadores = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		
		if(q.list().size()!=0) {
			jugadores =(ArrayList<Jugadores>) q.list();			
		}
		for (Jugadores jug : jugadores) {
			if(nick.equalsIgnoreCase(jug.getNick())) {
				//Si encuentra una con el mismo id, no la inserta y devuelve null
				return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
			}
		}
		Jugadores j = new Jugadores(nick,password,0,0,0);
		session.save(j);
		transaction.commit();
		//Si ha podido ser insertada la muestra
		return Response.status(Response.Status.OK).entity(g.toJson(j)).build();
	}
	
	
	/**
	 * Reemplaza un jugado por el que recibe como parametro en la base de datos
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response actualizarJugadores(@FormParam("jugador")String jugador) {
		ArrayList<Jugadores> jugadores = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		Jugadores j = g.fromJson(jugador, Jugadores.class);
		
		if(q.list().size()!=0) {
			jugadores =(ArrayList<Jugadores>) q.list();	
		}
		for (Jugadores jug : jugadores) {
			if(j.getNick().equalsIgnoreCase(jug.getNick())) {
				session.update(j);
				transaction.commit();
				//Si el jugador ha sido actualizado regresa el jugador cambiado
				return Response.status(Response.Status.OK).entity(g.toJson(j)).build();
			}
		}
		//Si no ha podido ser actualizado devuelve null
		return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
	}
	
	/**
	 * Elimina el jugador indicado de la base de datos
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response borrarJugadores(@FormParam("jugador")String jugador) {
		ArrayList<Jugadores> jugadores = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			jugadores =(ArrayList<Jugadores>) q.list();	
		}
		Jugadores j = g.fromJson(jugador, Jugadores.class);
		
		for (Jugadores jug : jugadores) {
			if(j.getNick().equalsIgnoreCase(jug.getNick())) {
				session.delete(jug);
				transaction.commit();
				//Regresa el objeto borrado
				return Response.status(Response.Status.OK).entity(g.toJson(jug)).build();
			}
		}
		//no pudo ser borrado
		return Response.status(Response.Status.OK).entity(null).build();
	}

}
