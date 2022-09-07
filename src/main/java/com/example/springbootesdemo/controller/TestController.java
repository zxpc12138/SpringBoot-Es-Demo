package com.example.springbootesdemo.controller;

import com.example.springbootesdemo.entity.FileEntiry;
import com.example.springbootesdemo.entity.Page;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author:钟湘
 * @data: 8:44
 */
@Controller
public class TestController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引
     */
    @RequestMapping("/createIndex")
    private void createIndex() {
        // 创建检索的对象,需要指定从哪里检索
        try{
            IndexOperations indexOperations=elasticsearchRestTemplate.indexOps(FileEntiry.class);
            if(!indexOperations.exists()){
               indexOperations.create();
               indexOperations.putMapping();//创建mapping,用于添加IK分词器
                upload("F:\\oo");
            }else{
                upload("F:\\oo");
            }
        }catch (IOException ioException){
            System.out.println(ioException.getStackTrace());
        }catch (Exception e){
            System.out.println("索引创建失败");
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * 查看分词器效果
     */
//    @RequestMapping("/test")
//    public void test() {
//        AnalyzeResponse analyzeResponse = elasticsearchRestTemplate.execute(
//                new ElasticsearchRestTemplate.ClientCallback<AnalyzeResponse>() {
//                    @Override
//                    public AnalyzeResponse doWithClient(RestHighLevelClient client) throws IOException {
//                        AnalyzeRequest analyzeRequest = AnalyzeRequest.withGlobalAnalyzer("ik_max_word", "aa");
//                        return client.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
//                    }
//                }
//        );
//        analyzeResponse.getTokens().forEach(analyzeToken -> {
//            System.out.println(analyzeToken.getTerm());
//        });
//    }

    @RequestMapping("/get")
    public void get(){
        NativeSearchQuery nativeSearchQuery=new NativeSearchQuery(QueryBuilders.matchQuery("fileContent","aaaaa"));

        SearchHits<FileEntiry> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, FileEntiry.class);

        List<SearchHit<FileEntiry>> list=searchHits.getSearchHits();

        System.out.println("查询到的总数据："+searchHits.getTotalHits());

        for (SearchHit<FileEntiry> lists:list) {
            FileEntiry fileEntiry=lists.getContent();
            System.out.println(fileEntiry.toString());
        }
    }


    @ResponseBody
    @RequestMapping("/page")
    public Page queryFile(String select, Page page){

        Integer pageCount = null;

        List<FileEntiry> files=new ArrayList<>();

        if(!select.equals("")){
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.should(QueryBuilders.matchQuery("fileName",select));
            queryBuilder.should(QueryBuilders.matchQuery("fileContent",select));

            //Pageable  SpringData  自带的分页对象
            Pageable pageable= PageRequest.of(page.getPageNo()-1, page.getPageSize());

            NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder()
                    .withQuery(queryBuilder)
                    .withPageable(pageable)
                    .withHighlightFields(new HighlightBuilder.Field("fileName").preTags("<span style='color:red'>").postTags("</span>"),
                            new HighlightBuilder.Field("fileContent").preTags("<span style='color:red'>").postTags("</span>"));

            SearchHits<FileEntiry> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), FileEntiry.class);

            List<SearchHit<FileEntiry>> list=searchHits.getSearchHits();

            System.out.println("查询到的总数据："+searchHits.getTotalHits());

            pageCount=Integer.valueOf((int) searchHits.getTotalHits());

            for (SearchHit<FileEntiry> lists:list) {
                FileEntiry fileEntiry=lists.getContent();

                List<String> fileName=lists.getHighlightField("fileName");
                List<String> fileContent=lists.getHighlightField("fileContent");

                if(fileName.size()==0){
                    fileName= Arrays.asList(fileEntiry.getFileName());
                }
                if(fileContent.size() ==0){
                    System.out.println("没有高亮");
                    fileContent=Arrays.asList(fileEntiry.getFileContent());
                    if(fileContent.get(0).length() >100){
                        String filecontents=fileEntiry.getFileContent().substring(0,100)+"...";
                        filecontents+=filecontents;
                        fileEntiry.setFileContent(filecontents);
                    }
                }else {
                        fileEntiry.setFileContent(fileContent.get(0));
                    if(fileContent.get(0).length() >100){
                        fileEntiry.setFileContent(fileContent.get(0)+"...");
                    }
                }

                fileEntiry.setFileName(String.valueOf(fileName));
                files.add(fileEntiry);

            }
        }else{

            Pageable pageable= PageRequest.of(page.getPageNo()-1, page.getPageSize());

            NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withPageable(pageable);

            SearchHits<FileEntiry> searchHits =elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(),FileEntiry.class);

            List<SearchHit<FileEntiry>> list=searchHits.getSearchHits();

            System.out.println("查询到的总数据："+searchHits.getTotalHits());

            pageCount=Integer.valueOf((int) searchHits.getTotalHits());

            for (SearchHit<FileEntiry> lists:list) {
                    FileEntiry fileEntiry=lists.getContent();
                if(fileEntiry.getFileContent().length() >100){
                    String filecontents=fileEntiry.getFileContent().substring(0,100)+"...";
                    fileEntiry.setFileContent(filecontents);
                }
                    files.add(fileEntiry);
            }
        }
        if(pageCount % page.getPageSize()==0){
            page.setPageCount(pageCount/ page.getPageSize());
        }else{
            page.setPageCount(pageCount/ page.getPageSize()+1);
        }
        page.setPageSizeSum(pageCount);
        page.setFiles(files);
        return page;
    }


    @RequestMapping("/query")
    public String query(Map<String,Object> map,String value){
        map.put("value",value);
        return "list";
    }

    /**
     * 循环获取文件
     * @param FilePath
     * @throws IOException
     */
    public void upload(String FilePath) throws IOException {
        File filepath = new File(FilePath);
        if (filepath.isDirectory()) {
            String[] files = filepath.list();
            for (String file : files) {
                upload(FilePath + File.separator + file);
            }
            System.out.println("文件夹" + filepath.getName());
        } else {
            if (filepath.getName().endsWith(".txt")) {
                System.out.println("我是" + filepath.getName() + "文件");

                FileEntiry fileEntiry=new FileEntiry();
                fileEntiry.setFileName(filepath.getName());
                fileEntiry.setFileSize(filepath.length());
                fileEntiry.setFileContent(readFile(filepath));
                fileEntiry.setFileUrl(filepath.getPath());

                elasticsearchRestTemplate.save(fileEntiry);
            }
        }
    }

    /**
     * 读取文件内容
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(File file) throws IOException {

//        String value="";
//        if(file.exists()&&file.isFile()){
//            InputStream inputStream=new FileInputStream(file);
//            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
//            String line=null;
//            if((line=bufferedReader.readLine())!=null){
//                value +=line;
//            }
//            bufferedReader.close();
//            inputStream.close();
//        }
//        return value;

        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        while ( (fileInputStream.read(bytes)) != -1);
        String value=new String(bytes);
        return value;
    }

    /**
     * 文件下载
     * @param response
     * @param loadFilePath
     * @throws IOException
     */
    @RequestMapping("/download")
    public void downloadFile(HttpServletResponse response, String loadFilePath) throws IOException {

        String[] filepath=loadFilePath.split("\\\\");

        String filename=filepath[filepath.length-1];

        filename = new String(filename.replaceAll(" ", "").getBytes("UTF-8"), "ISO8859-1");

        System.out.println(filename);

        response.setHeader("Content-Disposition","attachment;filename="+filename);

        response.addHeader("Content-Type","application/json;charset=UTF-8");

        FileInputStream fileInputStream=new FileInputStream(loadFilePath);

        OutputStream outputStream=response.getOutputStream();

        byte[] bytes=new byte[2048];
        int read=0;
        if((read=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,read);
        }
    }
}
