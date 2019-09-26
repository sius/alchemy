package liquer.alchemy.athanor.json;

import liquer.alchemy.athanor.json.Json.JObj;
import liquer.alchemy.athanor.json.Json.JSeq;
import liquer.alchemy.support.StringSupport;

import java.io.OutputStream;
import java.nio.charset.Charset;

public class JsonIndentWriter extends JsonWriter {
	
	private String indentString;
	private int indent = 0;
	
	public JsonIndentWriter(OutputStream out) {
		super(out);
		setIndentStringLength(Json.Singleton.CONFIG.getDefaultIndent());
	}
	public JsonIndentWriter(OutputStream out, int indent) {
		super(out);
		setIndentStringLength(indent);
	}
	public JsonIndentWriter(OutputStream out, int indent, int bufferSize) {
		super(out, bufferSize);
		setIndentStringLength(indent);
	}
	public JsonIndentWriter(OutputStream out, int indent, String charsetName) {
		super(out, charsetName);
		setIndentStringLength(indent);
	}
	public JsonIndentWriter(OutputStream out, int indent, String charsetName, int bufferSize) {
		super(out, charsetName, bufferSize);
		setIndentStringLength(indent);
	}
	public JsonIndentWriter(OutputStream out, int indent, Charset charset) {
		super(out, charset);
		setIndentStringLength(indent);
	}
	public JsonIndentWriter(OutputStream out, int indent, Charset charset, int bufferSize) {
		super(out, charset, bufferSize);
		setIndentStringLength(indent);
	}
	public String getIndentString() {
		return indentString;
	}
	public void setIndentStringLength(int indent) {
		this.indentString = StringSupport.multiply(' ', Math.max(0,  indent));
	}
	public int getIndent() {
		return indent;
	}
	public void indent() {
		indent++;
	}
	public void outdent() {
		if (indent > 0)
			indent--;
	}
	@Override 
	public void beginObj() {
		printIndentation();
		super.beginObj();
		println();
		indent();
	}
	@Override 
	public void beginSeq() {
		printIndentation();
		super.beginSeq();
		println();
		indent();
	}
	private void printIndentation() {
		for (int i = 0; i < indent; i++) {
			print(indentString); 
		}
	}
	@Override 
	public void name(String key) {
		printIndentation();
		super.name(key);
	}
	@Override 
	public void namedValue(String key, Json value) {
		name(key);
		if (value instanceof JObj || value instanceof JSeq) {
			println();
		}
		value.print(this);
	}
	@Override public void value(Json value) {
		if (!(value instanceof JObj || value instanceof JSeq)) {
			printIndentation();
		}
		value.print(this);
	}
	@Override 
	public void endObj() {
		println();
		outdent();
		printIndentation();
		super.endObj(); 
	}
	@Override 
	public void endSeq() {
		println();
		outdent();
		printIndentation();
		super.endSeq(); 
	}
	@Override
	public void comma() { 
		super.comma();
		println();
	}
	public void println() {
		print("\n");
	}
}
