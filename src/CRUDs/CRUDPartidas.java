package CRUDs;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
import Clases.Partidas;

@Path("/partidas")
public class CRUDPartidas extends ResourceConfig {
	
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPartidas() {
		ArrayList<Partidas> partidas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(partidas)).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response borrarPartidas(@FormParam("idPartida")String idPartida) {
		ArrayList<Partidas> partidas = new ArrayList<>();
		Gson g = new Gson();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			partidas =(ArrayList<Partidas>) q.list();			
		}
		int par = g.fromJson(idPartida, Integer.class);
		
		for (Partidas p : partidas) {
			if(p.getId() == par) {
				session.delete(p);
				transaction.commit();
				//Regresa el objeto borrado
				return Response.status(200).entity(g.toJson(p)).build();
			}
		}
		//no pudo ser borrado
		return Response.status(200).entity(null).build();
	}
}
