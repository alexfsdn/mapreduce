package br.com.mr.model;


import br.com.mr.enums.DelimitadorEnum;
import br.com.mr.model.enums.LoginEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Login implements Writable {
    private String matricula;
    private String cargo;



    public Login(String matricula, String cargo) {
        this.matricula = matricula;
        this.cargo = cargo;
    }


    public Login(){

    }


    public static Login parseFromText(Text text) {

        if (!StringUtils.isEmpty(text.toString())) {

            String[] line = text.toString().split(DelimitadorEnum.PIPE.name(), -1);

            if (line.length > 0) {
                String matricula = line[LoginEnum.matricula.ordinal()].trim();
                String cargo = line[LoginEnum.cargo.ordinal()].trim();

                return new Login(matricula, cargo);
            }
        }
        return null;
    }


    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.matricula);
        out.writeUTF(this.cargo);
    }

    public void readFields(DataInput in) throws IOException {
        this.matricula = in.readUTF();
        this.cargo = in.readUTF();
    }

    public void clean() {
        this.matricula = StringUtils.EMPTY;
        this.cargo = StringUtils.EMPTY;
    }


    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }


}
