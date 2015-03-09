package com.clickntap.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHUtils {
	public static final String MODE644 = "0644";
	public static final int DEFAULTPORT = 22;

	public static String exec(String hostname, String username, String password, String command) throws IOException {
		Connection conn = connect(hostname, username, password);
		String out = ConstUtils.EMPTY;
		out = exec(conn, command, out);
		conn.close();
		return out;
	}

	public static Connection connect(String hostname, String username, String password) throws IOException {
		int port = DEFAULTPORT;
		int offset;
		if ((offset = hostname.indexOf(ConstUtils.COLON)) >= 0) {
			port = Integer.parseInt(hostname.substring(offset + 1));
			hostname = hostname.substring(0, offset);
		}
		Connection conn = connect(hostname, port, username, password);
		return conn;
	}

	public static String exec(Connection conn, String command, String out) throws IOException {
		Session sess = conn.openSession();
		sess.execCommand(command);
		InputStream stdout = new StreamGobbler(sess.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		String line;
		while ((line = br.readLine()) != null) {
			out += line;
			out += ConstUtils.NEWLINE;
		}
		br.close();
		sess.close();
		return out;
	}

	public static String exec(Connection conn, String command) throws IOException {
		return exec(conn, command, ConstUtils.EMPTY);
	}

	public static Connection connect(String hostname, int port, String username, String password) throws IOException {
		Connection conn = new Connection(hostname, port);
		conn.connect();
		boolean isAuthenticated = conn.authenticateWithPassword(username, password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		return conn;
	}

	public static void scp(String hostname, String username, String password, String localFile, String remoteTargetDirectory, String mode) throws IOException {
		Connection conn = connect(hostname, username, password);
		SCPClient client = new SCPClient(conn);
		client.put(localFile, new File(localFile).length(), remoteTargetDirectory, mode);
		conn.close();
	}

	public static void scp(String hostname, String username, String password, String localFile, String remoteTargetDirectory) throws IOException {
		scp(hostname, username, password, localFile, remoteTargetDirectory, MODE644);
	}
}
