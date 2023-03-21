package org.springframework.samples.petclinic.records;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
class RecordsTransferController {

	private final Path recordsDir = Path.of("records");

	@PostMapping("/records-transfers")
	public NewRecordModel newRecordsTransfer(@RequestBody InputStream body)
		throws IOException {
		final var id = saveToRecordsSystem(body);
		return new NewRecordModel(id);
	}

	private String saveToRecordsSystem(final InputStream is) throws IOException {
		final var id = UUID.randomUUID().toString();
		final var path = recordsDir.resolve("record-" + id + ".json");
		Files.copy(is, path);
		return readRecordId(path);
	}

	private String readRecordId(final Path path) throws IOException {
		final var factory = XMLInputFactory.newFactory();
		try (var reader = Files.newBufferedReader(path)) {
			final var xmlEventReader = factory.createXMLEventReader(reader);
			while (xmlEventReader.hasNext()) {
				final var xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement() && "record-id".equals(
					xmlEvent.asStartElement().getName().getLocalPart())) {
					return xmlEventReader.nextEvent().asCharacters().getData();
				}
			}
		} catch (XMLStreamException e) {
			throw new IllegalArgumentException("Invalid XML", e);
		}
		throw new IllegalArgumentException("Invalid XML: no record-id element found");
	}

}
