package com.clickntap.tool.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.clickntap.tool.script.ScriptEngine;
import com.clickntap.utils.ConstUtils;

public class ScriptableSmartPdf extends SmartPdf {
	private ScriptEngine engine;
	private String outName;

	public ScriptableSmartPdf() throws Exception {
		// this.engine = new WebmacroScriptEngine();
		// ((WebmacroScriptEngine) engine).start();
		this.outName = "out.pdf";
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

	public void exec(File template) throws Exception {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put(ConstUtils.THIS, this);
		engine.evalScript(ctx, FileUtils.readFileToString(template));
		FileOutputStream out = new FileOutputStream(template.getParentFile().getCanonicalPath() + ConstUtils.SLASH + getOutName());
		copyTo(out);
		out.close();
	}

	public String getOutName() {
		return outName;
	}

	public void setOutName(String outName) {
		this.outName = outName;
	}
}
