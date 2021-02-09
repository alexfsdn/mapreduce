package br.com.mr.model;

import br.com.mr.enums.DelimitadorEnum;
import br.com.mr.model.enums.ResponsavelEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Responsavel implements Writable {
    private String matricula;
    private String matriculaResp;

    public Responsavel() {

    }

    public Responsavel(String matricula, String matriculaResp) {

        this.matricula = matricula;
        this.matriculaResp = matriculaResp;
    }

    public static Responsavel parseFromText(String text) {

        if (!StringUtils.isEmpty(text)) {

            String[] line = text.split(DelimitadorEnum.PONTO_E_VIRGULA.name(), -1);

            if (line.length > 0) {
                return new Responsavel(line[ResponsavelEnum.matricula.ordinal()].trim(), line[ResponsavelEnum.matricula_resp.ordinal()].trim());
            }

        }
        return null;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.matricula);
        out.writeUTF(this.matriculaResp);
    }

    public void readFields(DataInput in) throws IOException {
        this.matricula = in.readUTF();
        this.matriculaResp = in.readUTF();
    }


    public void clean() {
        this.matricula = StringUtils.EMPTY;
        this.matriculaResp = StringUtils.EMPTY;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMatriculaResp() {
        return matriculaResp;
    }

    public void setMatriculaResp(String matriculaResp) {
        this.matriculaResp = matriculaResp;
    }

}
