package org.colomoto.logicalmodel.io.rawfunctions;

import java.io.File;
import java.io.IOException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.AbstractFormat;
import org.colomoto.logicalmodel.io.LogicalModelFormat;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(LogicalModelFormat.class)
public class TextFunctionFormat extends AbstractFormat {

	public static final String ID = "rawfunctions";
	
	public TextFunctionFormat() {
		super(ID, "import raw functions", false, true);
	}

	
	@Override
	public LogicalModel importFile(File f) throws IOException {
		RawFunctionImport importer = new RawFunctionImport();
		importer.parse(f);
		
		return importer.getModel();
	}

}
