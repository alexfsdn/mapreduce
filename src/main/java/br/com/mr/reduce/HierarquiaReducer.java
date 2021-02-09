package br.com.mr.reduce;

import br.com.mr.enums.Counters;
import br.com.mr.enums.DelimitadorEnum;
import br.com.mr.model.Login;
import br.com.mr.model.Responsavel;
import br.com.mr.utils.UsuarioHierarquiaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class HierarquiaReducer extends Reducer<Text, Login, NullWritable, Text> {

    private static final String EXECUTIVO = "Executivo";
    private static final String GERENTE_REGIONAL = "Gerente Regional";
    private static final String HEAD_NACIONAL = "Head Nacional";
    private static final String DIRETOR_NACIONAL = "Diretor Nacional";

    private Login login;

    protected String responsavelPath;
    protected List<Responsavel> responsavelLst;
    protected Map<String, List<Responsavel>> mapResponsavel;

    protected List<List<String>> listaResponsaveis;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        this.login = new Login();
    }

    @Override
    protected void reduce(Text key, Iterable<Login> values, Context context) throws IOException, InterruptedException {

        for (Login v : values) {
            context.getCounter(Counters.REDUCER_COUNTER).increment(1l);
            this.login = v;

            String matricula = login.getMatricula();

            String responsavelImediato = Optional.ofNullable(this.mapResponsavel.get(matricula)).map(list -> list.get(0)).map(respItem -> respItem.getMatriculaResp()).orElse(StringUtils.EMPTY);

            listaResponsaveis = UsuarioHierarquiaUtil.hierarquiaBuild(matricula, mapResponsavel);

            String executivo = StringUtils.EMPTY;
            String gerenteRegional = StringUtils.EMPTY;
            String headNacional = StringUtils.EMPTY;
            String diretorNacional = StringUtils.EMPTY;

            for (int i = 0; i < listaResponsaveis.size(); i++) {
                for (int j = 0; j < listaResponsaveis.get(i).size(); j++) {
                    String responsavel = listaResponsaveis.get(i).get(j);
                    String valor = null; //Optional.ofNullable(this.mapLogin.get(responsavel)).map(list -> list.get(0)).map(item -> item.getCargo()).orElse(StringUtils.EMPTY);
                    if (valor.equals(EXECUTIVO)) {
                        if (executivo.length() > 0) {
                            if (!verificaDuplicidade(executivo, responsavel)) {
                                executivo = executivo + "; " + responsavel;
                            }
                        } else {
                            executivo = responsavel;
                        }
                    } else if (valor.equals(GERENTE_REGIONAL)) {
                        if (gerenteRegional.length() > 0) {
                            if (!verificaDuplicidade(gerenteRegional, responsavel)) {
                                gerenteRegional = gerenteRegional + "; " + responsavel;
                            }
                        } else {
                            gerenteRegional = responsavel;
                        }
                    } else if (valor.equals(HEAD_NACIONAL)) {
                        if (headNacional.length() > 0) {
                            if (!verificaDuplicidade(headNacional, responsavel)) {
                                headNacional = headNacional + "; " + responsavel;
                            }
                        } else {
                            headNacional = responsavel;
                        }
                    } else if (valor.equals(DIRETOR_NACIONAL)) {
                        if (diretorNacional.length() > 0) {
                            if (!verificaDuplicidade(diretorNacional, responsavel)) {
                                diretorNacional = diretorNacional + "; " + responsavel;
                            }
                        } else {
                            diretorNacional = responsavel;
                        }
                    }
                }
            }

            try {
                context.write(NullWritable.get(), writeOutput(matricula, responsavelImediato,
                        executivo, gerenteRegional, headNacional, diretorNacional));
                context.getCounter(Counters.REDUCER_WRITE).increment(1l);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean verificaDuplicidade(String matricula, String responsavel) {

        String[] matriculas = matricula.toString().split(";", -1);

        if (matriculas.length > 0) {
            for (String m :
                    matriculas) {
                if (m.equals(responsavel)) {
                    return true;
                }
            }
        }
        return false;
    }


    private Text writeOutput(String matricula, String matricula_resp,
                             String executivo, String gerenteRegional, String headNacional, String diretorNacional) {
        StringJoiner sj = new StringJoiner(DelimitadorEnum.PIPE.name());
        sj.add(matricula)
                .add(matricula_resp)
                .add(executivo)
                .add(gerenteRegional)
                .add(headNacional)
                .add(diretorNacional)
                .add(null);

        return new Text(sj.toString());
    }


}