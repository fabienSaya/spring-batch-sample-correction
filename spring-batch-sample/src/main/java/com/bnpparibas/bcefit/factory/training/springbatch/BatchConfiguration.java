package com.bnpparibas.bcefit.factory.training.springbatch;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //@Value("file:c:/tmp/input/users_input_*.csv")
    @Value("file:c:/temp/input_for_spring_batch_sample/users_input_*.csv")
    private Resource[] inputResources;

    // Definition du Job
    @Bean
    public Job sampleJob(){
        return jobBuilderFactory.get("sampleJob").incrementer(new RunIdIncrementer())
                .start(sampleStep()).next(removeFilesStep()).build();
    }

    // Definition de notre Step
    @Bean
    public Step sampleStep(){
        return stepBuilderFactory.get("sampleStep").<UserCSV, UserOut>chunk(2)
                .reader(multiResourceItemReader())
                .writer(writerJDBC())
                .processor(processor())
                .listener(new MyItemReaderListener())
                .listener(new MyItemWriterListener())
                .listener(new MyItemProcessorListener())
                //.taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step removeFilesStep(){
        RemoveProcessedFileTAsklet tAsklet = new RemoveProcessedFileTAsklet();
        tAsklet.setResources(inputResources);
        return stepBuilderFactory.get("removeFilesStep").tasklet(tAsklet).build();
    }


    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        return executor;
    }



    // Besoin d'un MultiResourceItemReader parcequ'on a plusieurs fichiers à paarser
    @Bean
    public MultiResourceItemReader<UserCSV> multiResourceItemReader(){
        MultiResourceItemReader<UserCSV> reader = new MultiResourceItemReader<>();
        //Resource[] inputResources = new Resource[] {fileSystemResource};
        reader.setResources(inputResources);
        reader.setDelegate(readerFlatFile());
        //reader.setStrict(true);
        return reader;
    }

    // Notre Reader pour lire les inputs
    @Bean
    FlatFileItemReader<UserCSV> readerFlatFile(){
        FlatFileItemReader<UserCSV> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setLineMapper(makeLineMapper());
        //reader.setStrict(true);
        return reader;
    }

    // Notre processor pour executer le traitement métier
    @Bean
    ItemProcessor<UserCSV, UserOut> processor(){
        return new ToUpperCaseProcessor();
    }

    // Notre Writer qui va enregistrer le UserOut dans la Base de données H2
    @Bean
    ItemWriter<UserOut> writerJDBC(){
        return new JdbcBatchItemWriterBuilder<UserOut>()
                // besoin d'un datasource
                .dataSource(myDataSource())
                // besoin d'un itemSqlParameterSourceProvider pour injecter les valeur dans la requête SQL
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                // besoin de la req SQL pour enregistrer le UserOut dans la table USER
                .sql("INSERT INTO users (uid, nom, prenom) VALUES (:uid, :nom, :prenom)")
                // on utilise un builder => la méthode build pour créer l'object
                .build();
    }

    // le datasource H2
    @Bean
    public DataSource myDataSource(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        //dataSource.setJdbcUrl("jdbc:h2:tcp://localhost/~/userdb");
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/userdb");
        dataSource.setUsername("userjava");
        dataSource.setPassword("userjava");
        return dataSource;
    }

    // JDBCTemplate
    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(myDataSource());
    }


    private DefaultLineMapper makeLineMapper(){
        DefaultLineMapper mapper = new DefaultLineMapper();
        mapper.setLineTokenizer(makeLineTokenizer());
        mapper.setFieldSetMapper(makeFiledSetMapper());
        return mapper;
    }

    // Créer un BeanWrapperFieldSetMapper
    private BeanWrapperFieldSetMapper makeFiledSetMapper(){
        BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
        // Je vais utiliser le POJO UserCSV pour le parsing du fichier
        mapper.setTargetType(UserCSV.class);
        return mapper;
    }

    // Pour splitter la ligne dans le fichier csv en plusieurs colomnes
    private DelimitedLineTokenizer makeLineTokenizer(){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        // ajouter le délimiteur | qui se trouve dans le fichier à parser
        tokenizer.setDelimiter("|");
        // ajouter les attributs qui constituent mon objet dans l'ordre
        tokenizer.setNames(new String[] {"uid", "prenom", "nom"});
        return tokenizer;
    }


    //=========================================================================





}
