package cn.teamcat.doreamon.controller.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;

public class DictEnumGenerator {
	private Map<String,List<Map<String,Object>>> map;
	private String packageName="cn.teamcat.doraemon.controller.db.dict";
	private String outputPath=".";
	private String className="DatasEnum";
	
	/**
	 * 从数据库中dictype和dicdata表获取配置信息，只读取dictype表字段DicTypeGene值为1的记录
	 */
	private void fetchData()
	{
		SqlSession session = SessionFactoryUtil.getSession();  
//        DicDataMapper dataMapper = session.getMapper(DicDataMapper.class);
//        DicTypeMapper typeMapper = session.getMapper(DicTypeMapper.class);
        List<Integer> typeList = new ArrayList<Integer>();
        typeList.add(1);
        for (int i = 12; i < 21; i++) {
			typeList.add(i);
		}
        typeList.add(24);
        typeList.add(25);
        Map<String,List<Map<String,Object>>> valueMap = new HashMap<String, List<Map<String,Object>>>();
        for (int i = 0; i < typeList.size(); i++) {
        	int type = typeList.get(i);
//        	valueMap.put(typeMapper.selectDicTypeName(type).get("DicTypeName"), dataMapper.selectDicDataValue(type));
		}
        map = valueMap;
        session.close();
    }
	
	public void generate() throws IOException
	{
		fetchData();
		String cl="\r\n";
		
		File file = new File(outputPath+File.separator+packageName.replace(".", File.separator));
		if(!file.exists())
			file.mkdirs();
		
		StringBuilder builder=new StringBuilder();
		builder.append("//this file is generated by "+this.getClass().getCanonicalName()+cl);
		builder.append("package "+packageName+";"+cl);
		builder.append("public enum "+className+" {"+cl);
		
		for(String type: map.keySet())
		{
			List<Map<String,Object>> valueList =  map.get(type);
			for (int i = 0; i < valueList.size(); i++) {
				Map<String,Object> vmap = valueList.get(i);
				String name=vmap.get("DicDataName").toString();
				String value=vmap.get("DicDataValue").toString();
				builder.append("\t "+type+"_"+name+"("+value+"),"+cl);
			}
			
//			for(NameValuePair pair: map.get(type))
//			{
//				String name=pair.getName();
//				String value=pair.getValue();
//			}
		}
		builder.setCharAt(builder.lastIndexOf(","), ';');
		
		builder.append(cl);
		builder.append("\t private int value;"+cl);
		builder.append("\t private "+className+"( int value ){ this.value=value; }"+cl);
		builder.append("\t public int getValue(){return this.value;}"+cl);
		//builder.append("\t public String getName(){ return this.name();}"+cl);
		builder.append("\t @Override public String toString() { return this.name()+\":\"+this.value; }"+cl);
		builder.append("}"+cl);	
		FileUtils.writeStringToFile(new File(file.getAbsolutePath()+File.separator+className+".java"), builder.toString());
		
//		HibernateUtil.closeFactory();
	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Start...");
		DictEnumGenerator gene=new DictEnumGenerator();
		gene.packageName="cn.teamcat.doraemon.controller.db.dict";
		gene.className="DatasEnum";
		gene.outputPath="./src/main/java/";
		gene.generate();
		System.out.println("Done");
	}
}