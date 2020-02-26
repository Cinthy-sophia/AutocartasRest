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
import Clases.Partidas;

@Path("/partidas")
public class CRUDPartidas extends ResourceConfig {
	
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	/**
	 * Muestra todas las partidas disponibles en la base de datos
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPartidas() {
		ArrayList<Partidas> partidas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Partidas");
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(partidas)).build();
	}
	/** 
	 * Inserta una nueva partida en la base de datos
	 * @param partida
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response insertarPartidas(@FormParam("partida")String partida ) {
		ArrayList<Partidas> partidas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Partidas");
		Gson g = new Gson();
		Partidas p = g.fromJson(partida, Partidas.class);
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
		for (Partidas par : partidas) {
			if(par.getId() == p.getId()) {
				//Si encuentra una con el mismo id, no la inserta y devuelve null
				return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
			}
		}
		
		session.save(p);
		transaction.commit();
		//Si ha podido ser insertada la muestra
		return Response.status(Response.Status.OK).entity(g.toJson(p)).build();
		
		
		
	}
	
	/**
	 * Reemplaza una partida por la que recibe como parametro en la base de datos
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response actualizarPartidas(@FormParam("partida")String partida) {
		ArrayList<Partidas> partidas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Partidas");
		Partidas p = g.fromJson(partida, Partidas.class);
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
		for (Partidas par : partidas) {
			if(par.getId() == p.getId()) {
				session.update(par);
				transaction.commit();
				//Si ha sido actualizada regresa la jugada cambiada
				return Response.status(Response.Status.OK).entity(g.toJson(par)).build();
			}
		}
		//Si no ha podido ser actualizada devuelve null
		return Response.status(Response.Status.OK).entity(g.toJson(null)).build();
	}
	/**
	 * Se encarga de borrar la partida cuyo id coincida con alguna de la base de datos
	 * @param idPartida
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response borrarPartidas(@FormParam("idPartida")String idPartida) {
		ArrayList<Partidas> partidas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Partidas");
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
		int par = g.fromJson(idPartida, Integer.class);
		
		for (Partidas p : partidas) {
			if(p.getId() == par) {
				session.delete(p);
				transaction.commit();
				//Regresa el objeto borrado
				return Response.status(Response.Status.OK).entity(g.toJson(p)).build();
			}
		}
		//no pudo ser borrado
		return Response.status(Response.Status.OK).entity(null).build();
	}
	
	
}
