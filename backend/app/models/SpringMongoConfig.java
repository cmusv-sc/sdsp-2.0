package models;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;

import com.mongodb.MongoClient;

@Configuration
public class SpringMongoConfig {

  public @Bean
  MongoDbFactory mongoDbFactory() throws Exception {
    return new SimpleMongoDbFactory(new MongoClient("localhost", 27017), "SDSP");
  }

  public @Bean
  MongoTemplate mongoTemplate() throws Exception {
    MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
    converter.setTypeMapper(new DefaultMongoTypeMapper(null)); // removes _class field

    MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);
    return mongoTemplate;
  }
}

