package org.springframework.samples.petclinic.records;

import io.openpixee.security.ZipSecurity;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class RecordsTransferController {

	private final Path recordsDir;
	private final DataSource dataSource;

	@Autowired
	RecordsTransferController(final DataSource dataSource) {
		recordsDir = Path.of("records");
		this.dataSource = dataSource;
	}

	@PostMapping("/records-transfers")
	public void newRecordsTransfer(@RequestParam("zipped") boolean zipped,
		@RequestBody InputStream body)
		throws IOException, SQLException {
		final InputStream is = zipped ? ZipSecurity.createHardenedInputStream(body) : body;
		try (is) {
			saveToRecordsSystem(is);
		}
	}

	private void saveToRecordsSystem(final InputStream is) throws IOException, SQLException {
		final var path = recordsDir.resolve("records.json");
		Files.copy(is, path);
		final var connection = dataSource.getConnection();
		String statementQueryStr = "INSERT INTO records (path) VALUES ?";
		final PreparedStatement statement = connection.prepareStatement(statementQueryStr);
		statement.setString(1, "" + path);
		statement.execute();
	}

}
