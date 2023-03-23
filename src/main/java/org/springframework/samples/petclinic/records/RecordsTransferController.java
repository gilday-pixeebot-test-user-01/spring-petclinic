package org.springframework.samples.petclinic.records;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
class RecordsTransferController {

	private final Path debugRecordsDir = Path.of("records");

	private final boolean debug;

	RecordsTransferController(@Value("${debug:false}") final boolean debug) {
		this.debug = debug;
	}

	@PostMapping("/records-transfers")
	public NewRecordModel newRecordsTransfer(@RequestBody InputStream body) throws IOException {
		final var copy = debugRecordsDir.resolve("record-" + UUID.randomUUID() + ".xml");
		if (debug) {
			// save a copy
			Files.copy(body, copy);
		}
		final String id;
		try (var is = debug ? Files.newInputStream(copy) : body) {
			id = readRecordId(is);
		}
		return new NewRecordModel(id);
	}

	private String readRecordId(final InputStream is) {
		final var factory = XMLInputFactory.newFactory();
		try {
			final var xmlEventReader = factory.createXMLEventReader(is);
			while (xmlEventReader.hasNext()) {
				final var xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement()
						&& "record-id".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
					return xmlEventReader.nextEvent().asCharacters().getData();
				}
			}
		}
		catch (XMLStreamException e) {
			throw new IllegalArgumentException("Invalid XML", e);
		}
		throw new IllegalArgumentException("Invalid XML: no record-id element found");
	}

}
