package com.uncleshaw;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * <p> lucene索引增删改查 </p>
 * <pre>
 *
 * </pre>
 *
 * @author yichen
 * @date 2021/8/16
 */
public class LuceneIndexTests {

    /**
     * 删除索引——全部删除
     * @throws Exception
     */
    @Test
    public  void deleteAll() throws  Exception{
        //获得indexWeiter对象
        IndexWriter indexWriter= this.getIndexWriter();
        //删除所有的索引
        indexWriter.deleteAll();
        indexWriter.close();
    }
    /**
     * 删除索引-按条件删除
     */
    @Test
    public void delete() throws Exception{
        //获得indexWeiter对象
        IndexWriter indexWriter= this.getIndexWriter();

        Term t=new Term("fileName","全文");
        TermQuery query=new TermQuery(t);
        //删除指定条件
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }
    /**
     * 更新索引
     * @throws Exception
     */
    @Test
    public  void  update() throws  Exception{
        //获得indexWeiter对象
        IndexWriter indexWriter= this.getIndexWriter();
        Document document =new Document();
        document.add(new TextField("filen","测试文件名", Field.Store.YES));
        document.add(new TextField("fileC","测试文件内容", Field.Store.YES));

        indexWriter.updateDocument(new Term("fileName","全文"),document,new IKAnalyzer());
        indexWriter.close();
    }
    /**
     * 公用的查询方法获取indexSearcher
     * @return
     * @throws Exception
     */
    public IndexSearcher getIndexSearcher() throws  Exception{

        //1：创建一个Directory对象，也就是索引库存放的位置
        Directory directory = FSDirectory.open(new File("./index"));
        //2:创建一个indexReader对象，需要制定Directory对象。
        IndexReader indexReader = DirectoryReader.open(directory);
        //3:创建一个indexSearcher对象，需要制定IndexReader对象
        return new IndexSearcher(indexReader);
    }
    /**
     * 查询所有
     * @throws Exception
     */
    @Test
    public void searchAll() throws  Exception{
        IndexSearcher indexSearcher =getIndexSearcher();

        Query query=new MatchAllDocsQuery();
        System.out.println("query::::"+query);
        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }
    /**
     * 数值范围查询
     */
    @Test
    public void numRangeSearch() throws  Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //查询条件：文件大小在1000到2500的，不包括1000，包含2500，设置参数可修改
        Query query=NumericRangeQuery.newLongRange("fileSize",1000L,2500L,false,true);

        System.out.println(query);
        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }
    /**
     * 组合查询
     */
    @Test
    public void searchMore() throws  Exception{
        IndexSearcher indexSearcher=getIndexSearcher();
        //查询名字中有bbb,"全文可有可无"
        BooleanQuery bc=new BooleanQuery();
        Query query1=new TermQuery(new Term("fileName","bbb"));
        Query query2=new TermQuery(new Term("fileName","全文"));
        bc.add(query1, BooleanClause.Occur.MUST);
        bc.add(query2, BooleanClause.Occur.SHOULD);

        System.out.println(bc);
        printResult(indexSearcher,bc);
        indexSearcher.getIndexReader().close();
    }
    /**
     *解析查询
     */
    @Test
    public void queryParser() throws Exception{
        IndexSearcher indexSearcher=getIndexSearcher();
        //参数1：默认查询的域，参数2：采用的分析器
        QueryParser queryParser=new QueryParser("fileName",new IKAnalyzer());
        Query query=queryParser.parse("fielName:aaabbb OR fileContent:what");
        printResult(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }
    /**
     * 增删改公用的获取IndexWriter对象
     * @return
     * @throws Exception
     */
    public IndexWriter getIndexWriter() throws Exception{
        //获得索引存放的位置
        Directory directory = FSDirectory.open(new File("./index"));
        //获得分词器
        Analyzer analyzer=new IKAnalyzer();
        //获得IndexWriterConfig对象
        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LUCENE_4_10_2,analyzer);
        //获得indexWeiter对象
        IndexWriter indexWriter =new IndexWriter(directory,indexWriterConfig);
        return  indexWriter;
    }
    /**
     * 公用的返回结果方法
     * @param indexSearcher
     * @param query
     * @throws Exception
     */
    public  void printResult(IndexSearcher indexSearcher, Query query) throws  Exception{
        //5：执行查询
        TopDocs topDocs =indexSearcher.search(query,10);
        //6:返回查询结果，便利查询结果且输出
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        for(ScoreDoc scoreDoc: scoreDocs){
            int doc=scoreDoc.doc;
            Document document =indexSearcher.doc(doc);
            //文件名
            String fileName=document.get("fileName");
            System.out.println("fileName:"+fileName);
            // 文件大小
            String fileSize = document.get("fileSize");
            System.out.println("fileSize:::"+fileSize);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println("filePath:::"+filePath);
            //文件内容
            // fileContent=document.get("fileContent");
            //System.out.println(fileContent);
            System.out.println("===============================");
        }
    }
}
