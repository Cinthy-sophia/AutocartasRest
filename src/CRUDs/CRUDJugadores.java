package CRUDs;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response insertarJugadores(@FormParam("jugador")String jugador ) {
		session = sessionF.openSession();
		transaction = session.beginTransaction();
		Query q = session.createQuery("from Jugadores");
		Gson g = new Gson();
		Jugadores j = g.fromJson(jugador, Jugadores.class);
		session.save(j);
		transaction.commit();
		return Response.status(Response.Status.OK).entity(g.toJson(j)).build();
		
	}

}
