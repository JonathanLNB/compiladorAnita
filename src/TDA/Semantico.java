package TDA;

public class Semantico {
    public boolean sameFamily(Token tk1, Token tk2) {
        int tipoTk1, tipoTk2;
        int familia1 = 0, familia2 = 0;
        tipoTk1 = tk1.getTipo();
        tipoTk2 = tk2.getTipo();
        familia1 = setFamily(tipoTk1);
        familia2 = setFamily(tipoTk2);
        if (familia1 == familia2)
            return true;
        return false;
    }

    public int setFamily(int type) {
        int family = 0;
        switch (type) {
            case 180:
            case 181:
                family = 1;
                break;
            case 182:
                family = 2;
                break;
        }
        return family;
    }

    public boolean isFloat(Token identificador, Token asignado) {
        if (sameFamily(identificador, asignado)) {
            if (identificador.getTipo() == 181) {
                if (identificador.getTipo() != asignado.getTipo())
                    return true;
                return false;
            }
            return false;
        }
        return false;
    }

    public String getDataType(int id) {
        switch (id) {
            case 180:
                return "Entero";
            case 181:
                return "Decimal";
            case 182:
                return "Cadena";
            default:
                return "";
        }
    }
}
