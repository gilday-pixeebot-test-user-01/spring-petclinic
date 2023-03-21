package org.springframework.samples.petclinic.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	@PostMapping("/records-transfers")
	public void newRecordsTransfer(@RequestParam("zipped") boolean zipped, @RequestBody InputStream body)
			throws IOException {
		final InputStream is = zipped ? new ZipInputStream(body) : body;
		try (is) {
			saveToRecordsSystem(is);
		}
	}

	private void saveToRecordsSystem(final InputStream is) throws IOException {
		final var factory = XMLInputFactory.newFactory();
		final XMLEventReader reader;
		try {
			reader = factory.createXMLEventReader(is);
		}
		catch (XMLStreamException e) {
			throw new IOException("Failed to read XML", e);
		}
		while (reader.hasNext()) {
			final XMLEvent event;
			try {
				event = reader.nextEvent();
			}
			catch (XMLStreamException e) {
				throw new IOException("Failed to read XML", e);
			}
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				final var startElement = event.asStartElement();
				if (!startElement.getName().getLocalPart().equals("ExternalVetRecord")) {
					throw new IOException("XML does not match expected schema");
				}
			}
		}
		is.transferTo(OutputStream.nullOutputStream());
	}

}
