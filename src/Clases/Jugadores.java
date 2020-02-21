package Clases;
// Generated 21 feb. 2020 17:59:05 by Hibernate Tools 5.2.12.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Jugadores generated by hbm2java
 */
public class Jugadores implements java.io.Serializable {

	private String nick;
	private String nombre;
	private String password;
	private int partidasGanadas;
	private int partidasPerdidas;
	private int partidasEmpatadas;
	private String sesionActual;
	private Set<Partidas> partidases = new HashSet<Partidas>(0);

	public Jugadores() {
	}

	public Jugadores(String nick, String password, int partidasGanadas, int partidasPerdidas, int partidasEmpatadas) {
		this.nick = nick;
		this.password = password;
		this.partidasGanadas = partidasGanadas;
		this.partidasPerdidas = partidasPerdidas;
		this.partidasEmpatadas = partidasEmpatadas;
	}

	public Jugadores(String nick, String nombre, String password, int partidasGanadas, int partidasPerdidas,
			int partidasEmpatadas, String sesionActual, Set<Partidas> partidases) {
		this.nick = nick;
		this.nombre = nombre;
		this.password = password;
		this.partidasGanadas = partidasGanadas;
		this.partidasPerdidas = partidasPerdidas;
		this.partidasEmpatadas = partidasEmpatadas;
		this.sesionActual = sesionActual;
		this.partidases = partidases;
	}

	public String getNick() {
		return this.nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPartidasGanadas() {
		return this.partidasGanadas;
	}

	public void setPartidasGanadas(int partidasGanadas) {
		this.partidasGanadas = partidasGanadas;
	}

	public int getPartidasPerdidas() {
		return this.partidasPerdidas;
	}

	public void setPartidasPerdidas(int partidasPerdidas) {
		this.partidasPerdidas = partidasPerdidas;
	}

	public int getPartidasEmpatadas() {
		return this.partidasEmpatadas;
	}

	public void setPartidasEmpatadas(int partidasEmpatadas) {
		this.partidasEmpatadas = partidasEmpatadas;
	}

	public String getSesionActual() {
		return this.sesionActual;
	}

	public void setSesionActual(String sesionActual) {
		this.sesionActual = sesionActual;
	}

	public Set<Partidas> getPartidases() {
		return this.partidases;
	}

	public void setPartidases(Set<Partidas> partidases) {
		this.partidases = partidases;
	}

}
