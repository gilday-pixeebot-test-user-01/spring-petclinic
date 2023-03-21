package org.springframework.samples.petclinic.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class RecordsTransferController {

	@PostMapping("/records-transfers")
	public void newRecordsTransfer(@RequestParam("zipped") boolean zipped, @RequestBody InputStream body)
			throws IOException {
		final InputStream is = zipped ? new ZipInputStream(body) : body;
		try (is) {
			saveToRecordsSystem(is);
		}
	}

	private void saveToRecordsSystem(final InputStream is) throws IOException {
		// TODO
		is.transferTo(OutputStream.nullOutputStream());
	}

}
