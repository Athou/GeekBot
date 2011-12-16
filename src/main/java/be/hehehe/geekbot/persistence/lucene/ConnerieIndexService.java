package be.hehehe.geekbot.persistence.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import be.hehehe.geekbot.persistence.dao.ConnerieDAO;
import be.hehehe.geekbot.persistence.model.Connerie;
import be.hehehe.geekbot.utils.BundleService;
import be.hehehe.geekbot.utils.LOG;

@Singleton
public class ConnerieIndexService {

	private static final Version VERSION = Version.LUCENE_35;

	@Inject
	private BundleService bundleService;

	public void startRebuildingIndexThread() {
		ScheduledExecutorService scheduler = Executors
				.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				rebuildIndex();
			}
		}, 5, 60 * 60 * 24, TimeUnit.SECONDS);
	}

	public void rebuildIndex() {
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig config = new IndexWriterConfig(VERSION,
					new FrenchAnalyzer(VERSION));
			config.setOpenMode(OpenMode.CREATE);
			indexWriter = new IndexWriter(openDirectory(), config);
			List<Connerie> conneries = new ConnerieDAO().findAll();
			int step = conneries.size() / 20;
			for (int i = 0; i < conneries.size() - 1; i++) {
				Document doc = new Document();
				doc.add(new Field("value", conneries.get(i).getValue(),
						Store.NO, Index.ANALYZED));
				doc.add(new Field("next", conneries.get(i + 1).getValue(),
						Store.YES, Index.NOT_ANALYZED));
				indexWriter.addDocument(doc);
				if (i % step == 0) {
					int perc = (i / step);
					perc = perc + 1;
					perc = perc * 5;
					if (perc <= 100) {
						LOG.info("Building index: " + perc + "%");
					}
				}
			}
			indexWriter.optimize();

		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(indexWriter);
		}

	}

	public List<String> findRelated(String keywords,
			List<String> otherMessages, int howMany) {
		List<String> matchingConnerie = new ArrayList<String>();
		IndexReader reader = null;
		IndexSearcher searcher = null;
		keywords = keywords + " " + StringUtils.join(otherMessages, " ");
		try {
			reader = IndexReader.open(openDirectory());
			searcher = new IndexSearcher(reader);
			QueryParser queryParser = new QueryParser(VERSION, "value",
					new FrenchAnalyzer(VERSION));

			Query query = queryParser.parse(QueryParser.escape(keywords));
			TopDocs documents = searcher.search(query, howMany);

			for (ScoreDoc scoreDoc : documents.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				String next = doc.get("next");
				matchingConnerie.add(next);
			}

		} catch (Exception e) {
			LOG.handle(e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(searcher);
		}
		return matchingConnerie;
	}

	private Directory openDirectory() throws IOException {
		return FSDirectory.open(new File(bundleService.getLuceneDirectory()));
	}

}
