package TDA;

public class Token {
    private int id;
    private String token;
    private int tipo;

    public Token() {
    }

    public Token(int id, String token, int tipo) {
        this.id = id;
        this.token = token;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
