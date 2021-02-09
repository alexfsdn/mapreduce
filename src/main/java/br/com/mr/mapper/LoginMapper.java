package br.com.mr.mapper;

import br.com.mr.enums.Counters;
import br.com.mr.model.Login;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


public class LoginMapper extends Mapper<LongWritable, Text, Text, Login> {

    private Login login;


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        login = new Login();
    }


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        login.clean();
        login.parseFromText(value);

        if (!StringUtils.isEmpty(login.getMatricula())) {
            context.write(new Text(login.getMatricula()), login);
            context.getCounter(Counters.LOGIN_MAPPER_WRITE).increment(1l);
        } else {
            context.getCounter(Counters.LOGIN_MAPPER_DISCART).increment(1l);
        }


    }
}
