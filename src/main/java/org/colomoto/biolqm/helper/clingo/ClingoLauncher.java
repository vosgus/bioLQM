package org.colomoto.biolqm.helper.clingo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.colomoto.biolqm.helper.HelperTool;

public class ClingoLauncher {

	public static final HelperTool CLINGO = new HelperTool("clingo");

	private final String program;
	private final ClingoResultHandler handler;

	private int n = 0;
	private boolean minsolutions = false;
	
	public static boolean isAvailable() {
		return CLINGO.isAvailable();
	}

	public ClingoLauncher(ClingoResultHandler handler, String program) {
		this.program = program;
		this.handler = handler;
	}

	public void setMinsolutions(boolean min) {
		this.minsolutions = min;
	}

	public void setSolutionLimit(int n) {
		this.n = n;
	}

	public void run() throws IOException {
		ProcessBuilder pb;
		if (minsolutions) {
			pb = CLINGO.getProcessBuilder("-n", "" + n, "--enum-mode=domRec", "--heuristic=Domain", "--dom-mod=3,16");
		} else {
			pb = CLINGO.getProcessBuilder("-n", "" + n);
		}

		if (pb == null) {
			System.out.println("% Clingo not found: print the code on stdout");
			System.out.println(program);
			return;
		}
		
		Process proc = pb.start();
		OutputStream stdin = proc.getOutputStream();
		BufferedReader reader = new BufferedReader( new InputStreamReader( proc.getInputStream()));
		stdin.write(program.getBytes());
		stdin.flush();
		stdin.close();

		boolean next_is_solution = false;
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (next_is_solution) {
				next_is_solution = false;
				ClingoResult result = new ClingoResult(line);
				handler.handle(result);
				continue;
			}
			if (line.startsWith("Answer:")) {
				next_is_solution = true;
			}
		}
	}
}

