package com.uncleshaw;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

class LuceneTests {

    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        //索引库存放的位置，也可以放在硬盘
        Directory directory = FSDirectory.open(new File("./index"));
        //标准的分词器
        // Analyzer analyzer = new StandardAnalyzer();
        // 中文分词器
        Analyzer analyzer =new IKAnalyzer();
        //创建输出流write
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //创建Filed域
        File f = new File("E:\\IdeaProject\\ShawNote\\public");
        //找到下面的所有待搜索的文件
        File[] listFiles = f.listFiles();
        iteration(listFiles, indexWriter);
        //关闭
        indexWriter.close();
    }

    /**
     * 查询索引
     *
     * @throws Exception
     */
    @Test
    public void searchIndex() throws Exception {
        //第一步，查询准备工作，创建Directory对象
        Directory dir = FSDirectory.open(new File("./index"));
        //创建IndexReader对象
        IndexReader reader = DirectoryReader.open(dir);
        //创建IndexSearch对象
        IndexSearcher search = new IndexSearcher(reader);

        //第二步，创建查询条件对象
        TermQuery query = new TermQuery(new Term("fileContent", "最大值"));
        //第三步：执行查询，参数（1：查询条件对象，2：查询结果返回的最大值）
        TopDocs topDocs = search.search(query, 10);
        //第四步：处理查询结果
        //输出结果数量
        System.out.println("查询结果数量：" + topDocs.totalHits);
        //取得结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("当前doc得分:" + scoreDoc.score);
            //根据文档对象ID取得文档对象
            Document doc = search.doc(scoreDoc.doc);
            System.out.println("文件名称：" + doc.get("fileName"));
            System.out.println("文件路径：" + doc.get("filePath"));
            System.out.println("文件大小：" + doc.get("fileSize"));
            // System.out.println("文件内容：" + doc.get("fileContent"));
            System.out.println("=======================================");
        }
        //关闭IndexReader对象
        reader.close();
    }

    @SneakyThrows
    public void iteration(File[] files, IndexWriter indexWriter) {
        for (File file : files) {
            if (file.isDirectory()) {
                iteration(file.listFiles(), indexWriter);
                continue;
            }
            //创建文档对象
            Document document = new Document();
            //文件名称
            String file_name = file.getName();
            Field fileNameFiled = new TextField("fileName", file_name, Field.Store.YES);
            //文件大小
            long file_size = FileUtils.sizeOf(file);
            Field fileSizeField = new LongField("fileSize", file_size, Field.Store.YES);
            //文件路径
            String file_path = file.getPath();
            Field filePathField = new StoredField("filePath", file_path);
            //文件内容
            String file_content = FileUtils.readFileToString(file, "utf8");
            Field fileContentField = new TextField("fileContent", file_content, Field.Store.YES);

            //保存到文件对象里
            document.add(fileNameFiled);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);

            //写到索引库
            indexWriter.addDocument(document);

        }
    }
}
