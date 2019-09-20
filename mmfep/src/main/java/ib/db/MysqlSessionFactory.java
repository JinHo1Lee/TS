package ib.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


public class MysqlSessionFactory {
	private BasicDataSource source;
	private SqlSessionFactoryBean sqlSessionFactoryBean;
	private SqlSessionTemplate sqlSessionTemplate;
	
	public MysqlSessionFactory(BasicDataSource source) throws IOException {
		// TODO Auto-generated constructor stub
		this.source = source;
		
	    sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(source);
        
        Properties properties = new Properties();
        properties.setProperty("defaultStatementTimeout", "3");
        
        sqlSessionFactoryBean.setConfigurationProperties(properties);
	}
	
	public SqlSessionFactoryBean getSqlSessionFactoryBean() {
		return sqlSessionFactoryBean;
	}

	public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
		this.sqlSessionFactoryBean = sqlSessionFactoryBean;
	}

	public void setMapperLocations(String mapper) throws IOException{
		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapper));
	}
		
	public SqlSessionTemplate getSqlSessionTemplate(){
		try {
			sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sqlSessionTemplate;
	}
	public void close() throws SQLException{
		sqlSessionTemplate.clearCache();
		sqlSessionTemplate.getConnection().close();
		source.close();			
	}
}
