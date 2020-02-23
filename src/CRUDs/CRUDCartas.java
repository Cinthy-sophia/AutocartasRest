package CRUDs;

import java.util.ArrayList;

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

import Clases.Cartas;
import Clases.HibernateUtil;
import Clases.Jugadores;

@Path("/cartas")
public class CRUDCartas extends ResourceConfig {
	
	private SessionFactory sessionF = HibernateUtil.getSessionFactory();
	private Session session;
	private Transaction transaction;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPartidas() {
		ArrayList<Cartas> cartas = new ArrayList<>();
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		
		if(q.list().size()!=0) {
			cartas =(ArrayList<Cartas>) q.list();			
		}
			
		Gson g = new Gson();
		return Response.status(Response.Status.OK).entity(g.toJson(cartas)).build();
	}
}
