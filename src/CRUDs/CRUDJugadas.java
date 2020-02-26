package CRUDs;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import Clases.HibernateUtil;
import Clases.Jugadas;
import Clases.Jugadores;
import Clases.Partidas;

@Path("/jugadas")
public class CRUDJugadas extends ResourceConfig {
	
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	/**
	 * Envia las jugadas disponibles en la base de datos
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerJugadas() {
		ArrayList<Jugadas> jugadas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadas");
		
		if(q.list().size()!=0) {
			jugadas =(ArrayList<Jugadas>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(jugadas)).build();
	}
	
	/** 
	 * Inserta una nueva jugada en la base de datos
	 * @param jugadas
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response insertarJugadas(@FormParam("jugada")String jugada ) {
		ArrayList<Jugadas> jugadas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadas");
		Gson g = new Gson();
		Jugadas j = g.fromJson(jugada, Jugadas.class);
		
		if(q.list().size()!=0) {
			jugadas =(ArrayList<Jugadas>) q.list();			
		}
		for (Jugadas jug : jugadas) {
			if(j.getIdJugada() == jug.getIdJugada()) {
				//Si encuentra una con el mismo id, no la inserta y devuelve null
				return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
			}
		}
		
		session.save(j);
		transaction.commit();
		//Si ha podido ser insertada la muestra
		return Response.status(Response.Status.OK).entity(g.toJson(j)).build();
		
	}
	
	/**
	 * Reemplaza una jugada por la que recibe como parametro en la base de datos
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response actualizarJugada(@FormParam("jugada")String jugada) {
		ArrayList<Jugadas> jugadas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadas");
		Jugadas j = g.fromJson(jugada, Jugadas.class);
		
		if(q.list().size()!=0) {
			jugadas =(ArrayList<Jugadas>) q.list();			
		}
		for (Jugadas jug : jugadas) {
			if(j.getIdJugada() == jug.getIdJugada()) {
				session.update(jug);
				transaction.commit();
				//Si ha sido actualizada regresa la jugada cambiada
				return Response.status(Response.Status.OK).entity(g.toJson(jug)).build();
			}
		}
		//Si no ha podido ser actualizada devuelve null
		return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
	}
	
	/**
	 * Elimina la jugada indicada de la base de datos
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response borrarJugada(@FormParam("jugada")String jugada) {
		ArrayList<Jugadas> jugadas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadas");
		
		if(q.list().size()!=0) {
			jugadas =(ArrayList<Jugadas>) q.list();			
		}
		Jugadas j = g.fromJson(jugada, Jugadas.class);
		
		for (Jugadas jug : jugadas) {
			if(jug.getIdJugada()== j.getIdJugada() ) {
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
