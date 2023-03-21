package org.springframework.samples.petclinic.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class RecordsTransferController {

	private final Path recordsDir = Path.of("/tmp/records");

	@PostMapping("/records-transfers")
	public void newRecordsTransfer(@RequestParam("zipped") boolean zipped, @RequestBody InputStream body)
			throws IOException {
		final InputStream is = zipped ? new ZipInputStream(body) : body;
		try (is) {
			saveToRecordsSystem(is);
		}
	}

	private void saveToRecordsSystem(final InputStream is) throws IOException {
		final var path = recordsDir.resolve("new-record.xml");
		final var os = Files.newOutputStream(path);
		is.transferTo(os);
	}

}
