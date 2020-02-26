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

import Clases.Cartas;
import Clases.HibernateUtil;
import Clases.Jugadores;
import Clases.Partidas;

@Path("/cartas")
public class CRUDCartas extends ResourceConfig {
	
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	
	/**
	 * Muestra todas las cartas disponibles en la base de datos
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerCartas() {
		ArrayList<Cartas> cartas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Cartas");
		
		if(q.list().size()!=0) {
			cartas =(ArrayList<Cartas>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(cartas)).build();
	}
	/** 
	 * Inserta una nueva carta en la base de datos
	 * @param carta
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response insertarCartas(@FormParam("carta")String carta ) {
		ArrayList<Cartas> cartas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Cartas");
		Gson g = new Gson();
		Cartas c = g.fromJson(carta, Cartas.class);
		if(q.list().size()!=0) {
			cartas =(ArrayList<Cartas>) q.list();			
		}
		for (Cartas car : cartas) {
			if(car.getIdCarta() == c.getIdCarta()) {
				//Si encuentra una con el mismo id, no la inserta y devuelve null
				return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
			}
		}
		
		session.save(c);
		transaction.commit();
		//Si ha podido ser insertada la muestra
		return Response.status(Response.Status.OK).entity(g.toJson(c)).build();
		
		
		
	}
	
	/**
	 * Reemplaza una partida por la que recibe como parametro en la base de datos
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response actualizarCartas(@FormParam("carta")String carta) {
		ArrayList<Cartas> cartas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Cartas");
		Cartas c = g.fromJson(carta, Cartas.class);
		
		if(q.list().size()!=0) {
			cartas =(ArrayList<Cartas>) q.list();			
		}
		for (Cartas car : cartas) {
			if(car.getIdCarta() == c.getIdCarta()) {
				session.update(car);
				transaction.commit();
				//Si ha sido actualizada regresa la jugada cambiada
				return Response.status(Response.Status.OK).entity(g.toJson(car)).build();
			}
		}
		//Si no ha podido ser actualizada devuelve null
		return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
	}
	/**
	 * Se encarga de borrar la carta cuyo id coincida con alguna de la base de datos
	 * @param idPartida
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response borrarCartas(@FormParam("carta")String carta) {
		ArrayList<Cartas> cartas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Cartas");
		
		if(q.list().size()!=0) {
			cartas =(ArrayList<Cartas>) q.list();			
		}
		Cartas c = g.fromJson(carta, Cartas.class);
		
		for (Cartas car : cartas) {
			if(car.getIdCarta() == c.getIdCarta()) {
				session.delete(car);
				transaction.commit();
				//Regresa el objeto borrado
				return Response.status(Response.Status.OK).entity(g.toJson(car)).build();
			}
		}
		//no pudo ser borrado
		return Response.status(Response.Status.OK).entity(null).build();
	}
}
