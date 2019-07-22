package Herramientas;

import TDA.Semantico;
import TDA.Token;
import TDA.Stack;

import java.util.ArrayList;

public class Simulador {
    private Acceso acceso;
    private AccesoCodigo accesoCodigo;
    private String[] alfabeto;
    private String[] auxV;
    private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<String> errores = new ArrayList<>();
    private String salida = "", texto = "", aux = "", erroresS = "";
    private char ultimoC;
    private int q, q0, x, y, id = 500, error = 0, errorS = 0, errorSema = 0, cantf, cont = 0, contS = 0, linea = 0;
    private int matriz[][] = new int[201][97];
    private int valores[] = {180, 181, 182};
    private int tiposDato[] = {150, 151, 154, 155};
    private int tipoRetorno[] = {150, 151, 154, 155, 179};
    private int tiposEncapsulamiento[] = {160, 161, 162};
    private int comparativos[] = {143, 144, 145, 146, 177, 178};
    private int aritmeticos[] = {135, 136, 169, 170, 171, 172, 173};
    private int igualacion[] = {175, 137, 138};
    private int logicos[] = {147, 148};
    private int agrupacion[] = {125, 126, 127, 128, 129, 130};
    private boolean bloquebool = false, sibool = false, forbool = false;

    public Simulador() {
        acceso = new Acceso("D:/Documentos/Tecno/8vo Semestre/Lenguajes y automatas 2/CompiladorAnita/Reglas.txt");
        accesoCodigo = new AccesoCodigo("D:/Documentos/Tecno/8vo Semestre/Lenguajes y automatas 2/CompiladorAnita/Codigo.gsh");
        aux = acceso.getLinea(0);
        alfabeto = aux.split("@");
        aux = acceso.getLinea(1);
        q0 = Integer.parseInt(aux);
        aux = acceso.getLinea(3);
        auxV = aux.split(",");
        int finales[] = new int[auxV.length];
        cantf = auxV.length;
        for (int i = 0; i < auxV.length; i++)
            finales[i] = Integer.parseInt(auxV[i]);
        aux = acceso.getLinea(2);
        auxV = aux.split(" ");
        x = Integer.parseInt(auxV[0]);
        y = Integer.parseInt(auxV[1]);
        for (int i = 0; i < x; i++) {
            aux = acceso.getLinea(i + 4);
            for (int j = 0; j < y; j++) {
                auxV = aux.split(",");
                matriz[i][j] = Integer.parseInt(auxV[j]);
            }
        }
        texto = accesoCodigo.getCodigo(9);
        analisisLexico(finales);
        analisisDelTipo();
        System.out.println("ID  |    Token    |  Tipo");
        System.out.println("--------------------------");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.print(tokens.get(i).getId());
            System.out.print(" | ");
            System.out.print(tokens.get(i).getToken());
            System.out.print(" | ");
            System.out.print(tokens.get(i).getTipo());
            System.out.print(" | ");
            System.out.println(encontrarToken(tokens.get(i).getId()));
        }
        cont = 0;
        analisisSintactico();
        if (errores.size() > 0) {
            for (int i = 0; i < errores.size(); i++) {
                System.out.println(errores.get(i));
            }
        } else {
            analisisSemantico();
            if (errores.size() > 0) {
                for (int i = 0; i < errores.size(); i++) {
                    System.out.println(errores.get(i));
                }
            } else {
                System.out.println("El codigo no tiene errores lexicos, ni sintacticos, ni semanticos\n");
            }
        }
    }

    int esFinal(int finales[]) {
        int salir = 0;
        for (int i = 0; i < cantf; i++) {
            if (finales[i] == q)
                if (finales[i] == 198)
                    salir = -1;
                else
                    salir = 1;
        }
        return salir;
    }

    boolean esTipoDato(int ID) {
        for (int i = 0; i <= 3; i++) {
            if (tiposDato[i] == ID)
                return true;
        }
        return false;
    }

    boolean esValor(int ID) {
        for (int i = 0; i <= 2; i++) {
            if (valores[i] == ID)
                return true;
        }
        return false;
    }

    boolean esTipoRetorno(int ID) {
        for (int i = 0; i <= 4; i++) {
            if (tipoRetorno[i] == ID)
                return true;
        }
        return false;
    }

    boolean esTipoEncapsulamiento(int ID) {
        for (int i = 0; i <= 2; i++) {
            if (tiposEncapsulamiento[i] == ID)
                return true;
        }
        return false;
    }

    boolean esComparativo(int ID) {
        for (int i = 0; i <= 5; i++) {
            if (comparativos[i] == ID)
                return true;
        }
        return false;
    }

    boolean esAritmetico(int ID) {
        for (int i = 0; i <= 6; i++) {
            if (aritmeticos[i] == ID)
                return true;
        }
        return false;
    }

    boolean esIgualacion(int ID) {
        for (int i = 0; i <= 2; i++) {
            if (igualacion[i] == ID)
                return true;
        }
        return false;
    }

    boolean esLogico(int ID) {
        for (int i = 0; i <= 1; i++) {
            if (logicos[i] == ID)
                return true;
        }
        return false;
    }

    boolean esAgrupacion(int ID) {
        for (int i = 0; i <= 5; i++) {
            if (agrupacion[i] == ID)
                return true;
        }
        return false;
    }

    int prioridad(char op) {
        switch (op) {
            case '^':
                return 3;
            case '*':
            case '/':
                return 2;
            case '+':
            case '-':
                return 1;
            case ')':
                return -1;
            default:
                return 0;
        }
    }

    String convertir(String in) {
        Stack pila = new Stack();
        String posf = "";
        for (int i = 0; i < in.length(); i++) {
            switch (in.charAt(i)) {
                case '(':
                    pila.push('(');
                    break;
                case ')':
                    while (!pila.isEmpty() && pila.top() != '(') {
                        posf += pila.top() + " ";
                        pila.pop();
                    }
                    pila.pop();
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '^':
                    while (!pila.isEmpty() && prioridad(in.charAt(i)) <= prioridad(pila.top())) {
                        posf += pila.top() + " ";
                        pila.pop();
                    }
                    pila.push(in.charAt(i));
                    break;
                default:
                    while (i < in.length() && (Character.isDigit(in.charAt(i)) || in.charAt(i) == '.'))
                        posf += in.charAt(i++);
                    posf += " ";
                    i--;
            }
        }
        while (!pila.isEmpty()) {
            posf += pila.top() + " ";
            pila.pop();
        }
        return posf;
    }

    String encontrarToken(int ID) {
        if (esTipoDato(ID))
            return "Tipo de dato";
        else if (esValor(ID))
            return "Valor (numerico o cadena)";
        else if (esTipoEncapsulamiento(ID))
            return "Metodo de encapsulamiento";
        else if (esTipoRetorno(ID))
            return "Tipo de retorno";
        else if (esLogico(ID))
            return "Operador Logico";
        else if (esAritmetico(ID))
            return "Operador Aritmetico";
        else if (esComparativo(ID))
            return "Operador Comparativo";
        else if (esIgualacion(ID))
            return "Operador de Asignacion";
        else if (esAgrupacion(ID))
            return "Operador de agrupacion";
        else if (ID >= 500)
            return "Variable";
        else if (ID == 134)
            return "Delimitador";
        else
            return "Palabra reservada";
    }

    int encontrarIndex(char aux) {
        for (int i = 0; i < alfabeto.length; i++) {
            if (alfabeto[i].charAt(0) == aux) {
                return i;
            }
        }
        return -1;
    }

    void analisisLexico(int finales[]) {
        String palabra = "", palabraAnt = "", erroresS = "";
        int index;
        boolean variable = false;
        int estado;
        String entrada[];
        System.out.println(texto);
        entrada = texto.split("\n");
        for (int i = 0; i < entrada.length; i++) {
            q = q0;
            texto = entrada[i];
            for (int j = 0; j <= texto.length(); j++) {
                estado = esFinal(finales);
                if (estado != -1) {
                    if (estado == 1 && q != 134) {
                        variable = true;
                        Token token = new Token();
                        palabraAnt = palabraAnt.replace(" ", "");
                        palabraAnt = palabraAnt.replace(";", "");
                        if (q == 182)
                            token.setToken(palabra);
                        else if (esAgrupacion(q)) {
                            palabra = palabra.replace(" ", "");
                            palabra = palabra.replace(";", "");
                            token.setToken(palabra);
                        } else if (q == 131)
                            token.setToken(palabra);
                        else
                            token.setToken(palabraAnt);
                        if (q == 199) {
                            for (int a = 0; a < tokens.size(); a++) {
                                if (tokens.get(a).getToken().equalsIgnoreCase(palabraAnt)) {
                                    token.setId(tokens.get(a).getId());
                                    variable = false;
                                }
                            }
                            if (variable) {
                                token.setId(id);
                                id++;
                            }
                        } else
                            token.setId(q);
                        tokens.add(token);
                        if (ultimoC == ';') {
                            token = new Token();
                            token.setId(134);
                            token.setToken(";");
                            tokens.add(token);
                        }
                        q = q0;
                        palabra = "";
                        palabraAnt = "";
                        j--;
                    } else {
                        if (q == 134) {
                            Token token = new Token();
                            token.setId(134);
                            token.setToken(";");
                            tokens.add(token);
                        } else {
                            if (j < texto.length()) {
                                index = encontrarIndex(texto.charAt(j));
                                if (index != -1) {
                                    palabraAnt = palabra;
                                    ultimoC = texto.charAt(j);
                                    palabra += ultimoC;
                                    q = matriz[q][index];
                                } else {
                                    erroresS = "Error lexico en la linea: " + (i + 1) + ":" + (j);
                                    errores.add(erroresS + "\n");
                                    error++;
                                    palabra = "";
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    erroresS = "Error lexico en la linea: " + (i + 1) + ":" + (j);
                    errores.add(erroresS + "\n");
                    error++;
                    palabra = "";
                    break;
                }
            }
        }
    }

    boolean incrementar() {
        if (cont < tokens.size() - 1) {
            cont++;
            return true;
        }
        return false;
    }

    boolean incrementarS() {
        if (contS < tokens.size() - 1) {
            contS++;
            return true;
        }
        return false;
    }

    void analisisDelBloque() {
        linea++;
        if (esTipoDato(tokens.get(cont).getId())) {
            declaracion();
        }
        if (tokens.get(cont).getId() == 164) {
            sibool = true;
            si();
        }
        if (tokens.get(cont).getId() == 163) {
            mandarSalida();
        }

        if (tokens.get(cont).getId() == 156) {
            lectura();
        }
        if (tokens.get(cont).getId() >= 500) {
            operacion();
        }
        if (tokens.get(cont).getId() == 128) {
            bloquebool = false;
            if (incrementar())
                analisisSintactico();
            else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " +
                        linea;
                errores.add(erroresS + "\n");
                return;
            }
        }
    }

    void analisisDelFor() {
        linea++;
        if (esTipoDato(tokens.get(cont).getId())) {
            declaracion();
        }
        if (tokens.get(cont).getId() == 164) {
            sibool = true;
            si();
        }
        if (tokens.get(cont).getId() == 157) {
            forbool = true;
            para();
        }
        if (tokens.get(cont).getId() == 163) {
            mandarSalida();
        }

        if (tokens.get(cont).getId() == 156) {
            lectura();
        }
        if (tokens.get(cont).getId() >= 500) {
            operacion();
        }
        if (tokens.get(cont).getId() == 128) {
            forbool = false;
            if (incrementar())
                analisisSintactico();
            else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " +
                        linea;
                errores.add(erroresS + "\n");
                return;
            }
        }
    }

    void lectura() {
        if (incrementar()) {
            if (tokens.get(cont).getId() == 125) {
                do {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() >= 500) {
                            if (!incrementar()) {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } while (tokens.get(cont).getId() == 131);
                if (tokens.get(cont).getId() == 130) {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() == 134) {
                            if (!incrementar()) {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                    return;
                }
            } else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " + linea;
                errores.add(erroresS + "\n");
                return;
            }
        } else {
            errorS++;
            erroresS = "Error de sintaxis en la linea: " + linea;
            errores.add(erroresS + "\n");
            return;
        }
    }

    void analisisDelSi() {
        linea++;
        if (esTipoDato(tokens.get(cont).getId())) {
            declaracion();
        }
        if (tokens.get(cont).getId() == 156) {
            lectura();
        }
        if (tokens.get(cont).getId() == 163) {
            mandarSalida();
        }
        if (tokens.get(cont).getId() >= 500) {
            operacion();
        }
        if (tokens.get(cont).getId() == 128) {
            sibool = false;
            if (incrementar()) {
                if (bloquebool)
                    analisisDelBloque();
                else
                    analisisSintactico();
            }
        } else {
            linea++;
            if (sibool) {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " + linea;
                errores.add(erroresS + "\n");
                return;
            }
        }

    }

    void declaracion() {
        if (incrementar()) {
            if (tokens.get(cont).getId() >= 500) {
                if (incrementar()) {
                    if (tokens.get(cont).getId() == 175) {
                        if (incrementar()) {
                            if (esValor(tokens.get(cont).getId())) {
                                if (incrementar()) {
                                    if (tokens.get(cont).getId() == 134) {
                                        if (consultarFin()) {
                                            analisisSintactico();
                                        }
                                        return;
                                    } else {
                                        if (tokens.get(cont).getId() == 131)
                                            declaracion();
                                        else {
                                            if (esAritmetico(tokens.get(cont).getId())) {
                                                do {
                                                    if (incrementar()) {
                                                        if (tokens.get(cont).getId() >= 500 || esValor(tokens.get(cont).getId())) {
                                                            if (incrementar()) {
                                                                if (!esAritmetico(tokens.get(cont).getId())) {
                                                                    if (tokens.get(cont).getId() == 134) {
                                                                        if (incrementar())
                                                                            analisisSintactico();
                                                                        return;
                                                                    } else {
                                                                        errorS++;
                                                                        erroresS = "Error de sintaxis en la linea: " +
                                                                                linea;
                                                                        errores.add(erroresS + "\n");
                                                                        return;
                                                                    }
                                                                }
                                                            } else {
                                                                errorS++;
                                                                erroresS = "Error de sintaxis en la linea: " +
                                                                        linea;
                                                                errores.add(erroresS + "\n");
                                                                return;
                                                            }
                                                        } else {
                                                            errorS++;
                                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                                            errores.add(erroresS + "\n");
                                                            return;
                                                        }
                                                    } else {
                                                        errorS++;
                                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                                        errores.add(erroresS + "\n");
                                                        return;
                                                    }
                                                } while (tokens.get(cont).getId() != 134 && tokens.get(cont).getId() != 131);
                                                if (tokens.get(cont).getId() == 134) {
                                                    if (consultarFin()) return;
                                                } else {
                                                    if (tokens.get(cont).getId() == 131)
                                                        declaracion();
                                                    else {
                                                        errorS++;
                                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                                        errores.add(erroresS + "\n");
                                                        return;
                                                    }
                                                }
                                            } else {
                                                errorS++;
                                                erroresS = "Error de sintaxis en la linea: " + linea;
                                                errores.add(erroresS + "\n");
                                                return;
                                            }
                                        }
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            } else {
                                if (tokens.get(cont).getId() >= 500) {
                                    for (int a = 0; a < tokens.size(); a++) {
                                        if (tokens.get(a).getId() == tokens.get(cont).getId()) {
                                            if (incrementar()) {
                                                if (tokens.get(cont).getId() == 134) {
                                                    if (consultarFin()) return;
                                                } else {
                                                    errorS++;
                                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                                    errores.add(erroresS + "\n");
                                                    return;
                                                }
                                            } else {
                                                errorS++;
                                                erroresS = "Error de sintaxis en la linea: " + linea;
                                                errores.add(erroresS + "\n");
                                                return;
                                            }
                                        }
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            }
                        }
                    } else {
                        if (tokens.get(cont).getId() == 131) {
                            declaracion();
                        }
                        if (tokens.get(cont).getId() == 134 || tokens.get(cont).getId() == 128) {
                            if (incrementar()) {
                                if (bloquebool)
                                    if (sibool)
                                        analisisDelSi();
                                    else
                                        analisisDelBloque();
                                else if (sibool)
                                    analisisDelSi();
                                else
                                    analisisSintactico();
                            }
                            return;
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                }
            } else {
                if (esTipoDato(tokens.get(cont).getId())) {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() == 175) {
                            if (incrementar()) {
                                if (esValor(tokens.get(cont).getId())) {
                                    if (consultarFin()) return;
                                } else {
                                    for (int a = 0; a < tokens.size(); a++) {
                                        if (tokens.get(a).getId() == tokens.get(cont).getId()) {
                                            if (consultarFin()) return;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (tokens.get(cont).getId() == 131) {
                                declaracion();
                            }
                            if (tokens.get(cont).getId() == 134 || tokens.get(cont).getId() == 128) {
                                if (incrementar()) {
                                    if (bloquebool)
                                        if (sibool)
                                            analisisDelSi();
                                        else
                                            analisisDelBloque();
                                    else if (sibool)
                                        analisisDelSi();
                                    else
                                        analisisSintactico();
                                }
                                return;
                            } else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                }
            }
        } else {
            errorS++;
            erroresS = "Error de sintaxis en la linea: " + linea;
            errores.add(erroresS + "\n");
        }
    }

    void bloque() {
        if (incrementar()) {
            if (esTipoRetorno(tokens.get(cont).getId())) {
                if (incrementar()) {
                    if (tokens.get(cont).getId() >= 500) {
                        if (incrementar()) {
                            if (tokens.get(cont).getId() == 125) {
                                do {
                                    if (incrementar()) {
                                        if (esTipoDato(tokens.get(cont).getId())) {
                                            if (incrementar()) {
                                                if (tokens.get(cont).getId() < 500) {
                                                    errorS++;
                                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                                    errores.add(erroresS + "\n");
                                                    return;
                                                }
                                                if (!incrementar()) {
                                                    errorS++;
                                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                                    errores.add(erroresS + "\n");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                } while (tokens.get(cont).getId() == 131);
                                if (tokens.get(cont).getId() == 130) {
                                    if (incrementar()) {
                                        if (tokens.get(cont).getId() == 127) {
                                            if (incrementar()) {
                                                analisisDelBloque();
                                            }
                                        } else {
                                            errorS++;
                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                            errores.add(erroresS + "\n");
                                            return;
                                        }
                                    } else {
                                        errorS++;
                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                        errores.add(erroresS + "\n");
                                        return;
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void operacion() {
        if (incrementar()) {
            if (esIgualacion(tokens.get(cont).getId())) {
                if (tokens.get(cont).getId() == 175) {
                    do {
                        if (incrementar()) {
                            if (tokens.get(cont).getId() >= 500 || esValor(tokens.get(cont).getId())) {
                                if (incrementar()) {
                                    if (!esAritmetico(tokens.get(cont).getId())) {
                                        if (tokens.get(cont).getId() == 134) {
                                            if (incrementar())
                                                analisisSintactico();
                                            return;
                                        } else {
                                            errorS++;
                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                            errores.add(erroresS + "\n");
                                            return;
                                        }
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            } else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } while (tokens.get(cont).getId() != 134);
                }
                if (tokens.get(cont).getId() == 137 || tokens.get(cont).getId() == 138) {
                    if (incrementar()) {
                        if (!(tokens.get(cont).getId() >= 500 || esValor(tokens.get(cont).getId()))) {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    }
                }
            }
        }
    }

    void mandarSalida() {
        if (incrementar()) {
            if (tokens.get(cont).getId() == 125) {
                do {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() == 181 || tokens.get(cont).getId() == 182 || tokens.get(cont).getId() >= 500) {
                            if (!incrementar()) {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } while (tokens.get(cont).getId() == 169);
                if (tokens.get(cont).getId() == 130) {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() == 134) {
                            if (!incrementar()) {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                    return;
                }
            } else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " + linea;
                errores.add(erroresS + "\n");
                return;
            }
        } else {
            errorS++;
            erroresS = "Error de sintaxis en la linea: " + linea;
            errores.add(erroresS + "\n");
            return;
        }
    }

    void si() {
        if (incrementar()) {
            if (tokens.get(cont).getId() == 125) {
                do {
                    if (incrementar()) {
                        if (esValor(tokens.get(cont).getId()) || tokens.get(cont).getId() >= 500) {
                            if (incrementar()) {
                                if (esComparativo(tokens.get(cont).getId()) && tokens.get(cont).getId() != 175) {
                                    if (incrementar()) {
                                        if (esValor(tokens.get(cont).getId()) || tokens.get(cont).getId() >= 500) {
                                            if (!incrementar()) {
                                                errorS++;
                                                erroresS = "Error de sintaxis en la linea: " + linea;
                                                errores.add(erroresS + "\n");
                                                return;
                                            }
                                        } else {
                                            errorS++;
                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                            errores.add(erroresS + "\n");
                                            return;
                                        }
                                    } else {
                                        errorS++;
                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                        errores.add(erroresS + "\n");
                                        return;
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            } else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } while (esLogico(tokens.get(cont).getId()));
                if (tokens.get(cont).getId() == 130) {
                    if (incrementar()) {
                        if (tokens.get(cont).getId() == 127) {
                            if (incrementar())
                                analisisDelSi();
                            else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        errorS++;
                        erroresS = "Error de sintaxis en la linea: " + linea;
                        errores.add(erroresS + "\n");
                        return;
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                    return;
                }
            } else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " + linea;
                errores.add(erroresS + "\n");
                return;
            }
        } else {
            errorS++;
            erroresS = "Error de sintaxis en la linea: " + linea;
            errores.add(erroresS + "\n");
            return;
        }
    }

    void para() {
        if (incrementar()) {
            if (tokens.get(cont).getId() == 125) {
                if (incrementar()) {
                    if (tokens.get(cont).getId() >= 500) {
                        if (incrementar()) {
                            if (tokens.get(cont).getId() == 175) {
                                if (incrementar()) {
                                    if (esValor(tokens.get(cont).getId()) || tokens.get(cont).getId() >= 500) {
                                        if (incrementar()) {
                                            if (tokens.get(cont).getId() == 131) {
                                                do {
                                                    if (incrementar()) {
                                                        if (esValor(tokens.get(cont).getId()) || tokens.get(cont).getId() >= 500) {
                                                            if (incrementar()) {
                                                                if (esComparativo(tokens.get(cont).getId())) {
                                                                    if (incrementar()) {
                                                                        if (esValor(tokens.get(cont).getId()) ||
                                                                                tokens.get(cont).getId() >= 500) {
                                                                            if (!incrementar()) {
                                                                                errorS++;
                                                                                erroresS =
                                                                                        "Error de sintaxis en la linea: " +
                                                                                                linea;
                                                                                errores.add(erroresS + "\n");
                                                                                return;
                                                                            }
                                                                        } else {
                                                                            errorS++;
                                                                            erroresS = "Error de sintaxis en la linea: " +
                                                                                    linea;
                                                                            errores.add(erroresS + "\n");
                                                                            return;
                                                                        }
                                                                    } else {
                                                                        errorS++;
                                                                        erroresS = "Error de sintaxis en la linea: " +
                                                                                linea;
                                                                        errores.add(erroresS + "\n");
                                                                        return;
                                                                    }
                                                                } else {
                                                                    errorS++;
                                                                    erroresS = "Error de sintaxis en la linea: " +
                                                                            linea;
                                                                    errores.add(erroresS + "\n");
                                                                    return;
                                                                }
                                                            } else {
                                                                errorS++;
                                                                erroresS = "Error de sintaxis en la linea: " +
                                                                        linea;
                                                                errores.add(erroresS + "\n");
                                                                return;
                                                            }
                                                        } else {
                                                            errorS++;
                                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                                            errores.add(erroresS + "\n");
                                                            return;
                                                        }
                                                    } else {
                                                        errorS++;
                                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                                        errores.add(erroresS + "\n");
                                                        return;
                                                    }
                                                } while (esLogico(tokens.get(cont).getId()));
                                                if (tokens.get(cont).getId() == 131) {
                                                    if (incrementar()) {
                                                        if (tokens.get(cont).getId() >= 500) {
                                                            if (incrementar()) {
                                                                if (tokens.get(cont).getId() == 135) {
                                                                    if (incrementar()) {
                                                                        if (tokens.get(cont).getId() == 130) {
                                                                            if (incrementar()) {
                                                                                if (tokens.get(cont).getId() == 127) {
                                                                                    if (incrementar())
                                                                                        analisisDelFor();
                                                                                    else {
                                                                                        errorS++;
                                                                                        erroresS =
                                                                                                "Error de sintaxis en la linea: " +
                                                                                                        linea;
                                                                                        errores.add(erroresS + "\n");
                                                                                        return;
                                                                                    }
                                                                                } else {
                                                                                    errorS++;
                                                                                    erroresS =
                                                                                            "Error de sintaxis en la linea: " +
                                                                                                    linea;
                                                                                    errores.add(erroresS + "\n");
                                                                                    return;
                                                                                }
                                                                            } else {
                                                                                errorS++;
                                                                                erroresS =
                                                                                        "Error de sintaxis en la linea: " +
                                                                                                linea;
                                                                                errores.add(erroresS + "\n");
                                                                                return;
                                                                            }
                                                                        } else {
                                                                            errorS++;
                                                                            erroresS = "Error de sintaxis en la linea: " +
                                                                                    linea;
                                                                            errores.add(erroresS + "\n");
                                                                            return;
                                                                        }
                                                                    } else {
                                                                        errorS++;
                                                                        erroresS = "Error de sintaxis en la linea: " +
                                                                                linea;
                                                                        errores.add(erroresS + "\n");
                                                                        return;
                                                                    }
                                                                } else {
                                                                    errorS++;
                                                                    erroresS = "Error de sintaxis en la linea: " +
                                                                            linea;
                                                                    errores.add(erroresS + "\n");
                                                                    return;
                                                                }
                                                            } else {
                                                                errorS++;
                                                                erroresS = "Error de sintaxis en la linea: " +
                                                                        linea;
                                                                errores.add(erroresS + "\n");
                                                                return;
                                                            }
                                                        } else {
                                                            errorS++;
                                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                                            errores.add(erroresS + "\n");
                                                            return;
                                                        }
                                                    } else {
                                                        errorS++;
                                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                                        errores.add(erroresS + "\n");
                                                        return;
                                                    }
                                                } else {
                                                    errorS++;
                                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                                    errores.add(erroresS + "\n");
                                                    return;
                                                }
                                            } else {
                                                errorS++;
                                                erroresS = "Error de sintaxis en la linea: " + linea;
                                                errores.add(erroresS + "\n");
                                                return;
                                            }
                                        } else
                                            errorS++;
                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                        errores.add(erroresS + "\n");
                                        return;
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            } else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    } else {
                        if (esTipoDato(tokens.get(cont).getId())) {
                            if (incrementar()) {
                                if (tokens.get(cont).getId() >= 500) {
                                    if (incrementar()) {
                                        if (tokens.get(cont).getId() == 175) {
                                            if (incrementar()) {
                                                if (esValor(tokens.get(cont).getId()) || tokens.get(cont).getId() >= 500) {
                                                    if (incrementar()) {
                                                        if (tokens.get(cont).getId() == 131) {
                                                            do {
                                                                if (incrementar()) {
                                                                    if (esValor(tokens.get(cont).getId()) ||
                                                                            tokens.get(cont).getId() >= 500) {
                                                                        if (incrementar()) {
                                                                            if (esComparativo(tokens.get(cont).getId())) {
                                                                                if (incrementar()) {
                                                                                    if (esValor(tokens.get(cont).getId()) ||
                                                                                            tokens.get(cont).getId() >= 500) {
                                                                                        if (!incrementar()) {
                                                                                            errorS++;
                                                                                            erroresS =
                                                                                                    "Error de sintaxis en la linea: " +
                                                                                                            linea;
                                                                                            errores.add(
                                                                                                    erroresS + "\n");
                                                                                            return;
                                                                                        }
                                                                                    } else {
                                                                                        errorS++;
                                                                                        erroresS =
                                                                                                "Error de sintaxis en la linea: " +
                                                                                                        linea;
                                                                                        errores.add(erroresS + "\n");
                                                                                        return;
                                                                                    }
                                                                                } else {
                                                                                    errorS++;
                                                                                    erroresS =
                                                                                            "Error de sintaxis en la linea: " +
                                                                                                    linea;
                                                                                    errores.add(erroresS + "\n");
                                                                                    return;
                                                                                }
                                                                            } else {
                                                                                errorS++;
                                                                                erroresS =
                                                                                        "Error de sintaxis en la linea: " +
                                                                                                linea;
                                                                                errores.add(erroresS + "\n");
                                                                                return;
                                                                            }
                                                                        } else {
                                                                            errorS++;
                                                                            erroresS = "Error de sintaxis en la linea: " +
                                                                                    linea;
                                                                            errores.add(erroresS + "\n");
                                                                            return;
                                                                        }
                                                                    } else {
                                                                        errorS++;
                                                                        erroresS = "Error de sintaxis en la linea: " +
                                                                                linea;
                                                                        errores.add(erroresS + "\n");
                                                                        return;
                                                                    }
                                                                } else {
                                                                    errorS++;
                                                                    erroresS = "Error de sintaxis en la linea: " +
                                                                            linea;
                                                                    errores.add(erroresS + "\n");
                                                                    return;
                                                                }
                                                            } while (esLogico(tokens.get(cont).getId()));
                                                            if (tokens.get(cont).getId() == 131) {
                                                                if (incrementar()) {
                                                                    if (tokens.get(cont).getId() >= 500) {
                                                                        if (incrementar()) {
                                                                            if (tokens.get(cont).getId() == 135) {
                                                                                if (incrementar()) {
                                                                                    if (tokens.get(cont).getId() == 130) {
                                                                                        if (incrementar()) {
                                                                                            if (tokens.get(cont).getId() ==
                                                                                                    127) {
                                                                                                if (incrementar())
                                                                                                    analisisDelFor();
                                                                                                else {
                                                                                                    errorS++;
                                                                                                    erroresS =
                                                                                                            "Error de sintaxis en la linea: " +
                                                                                                                    linea;
                                                                                                    errores.add(
                                                                                                            erroresS +
                                                                                                                    "\n");
                                                                                                    return;
                                                                                                }
                                                                                            } else {
                                                                                                errorS++;
                                                                                                erroresS =
                                                                                                        "Error de sintaxis en la linea: " +
                                                                                                                linea;
                                                                                                errores.add(
                                                                                                        erroresS + "\n");
                                                                                                return;
                                                                                            }
                                                                                        } else {
                                                                                            errorS++;
                                                                                            erroresS =
                                                                                                    "Error de sintaxis en la linea: " +
                                                                                                            linea;
                                                                                            errores.add(
                                                                                                    erroresS + "\n");
                                                                                            return;
                                                                                        }
                                                                                    } else {
                                                                                        errorS++;
                                                                                        erroresS =
                                                                                                "Error de sintaxis en la linea: " +
                                                                                                        linea;
                                                                                        errores.add(erroresS + "\n");
                                                                                        return;
                                                                                    }
                                                                                } else {
                                                                                    errorS++;
                                                                                    erroresS =
                                                                                            "Error de sintaxis en la linea: " +
                                                                                                    linea;
                                                                                    errores.add(erroresS + "\n");
                                                                                    return;
                                                                                }
                                                                            } else {
                                                                                errorS++;
                                                                                erroresS =
                                                                                        "Error de sintaxis en la linea: " +
                                                                                                linea;
                                                                                errores.add(erroresS + "\n");
                                                                                return;
                                                                            }
                                                                        } else {
                                                                            errorS++;
                                                                            erroresS = "Error de sintaxis en la linea: " +
                                                                                    linea;
                                                                            errores.add(erroresS + "\n");
                                                                            return;
                                                                        }
                                                                    } else {
                                                                        errorS++;
                                                                        erroresS = "Error de sintaxis en la linea: " +
                                                                                linea;
                                                                        errores.add(erroresS + "\n");
                                                                        return;
                                                                    }
                                                                } else {
                                                                    errorS++;
                                                                    erroresS = "Error de sintaxis en la linea: " +
                                                                            linea;
                                                                    errores.add(erroresS + "\n");
                                                                    return;
                                                                }
                                                            } else {
                                                                errorS++;
                                                                erroresS = "Error de sintaxis en la linea: " +
                                                                        linea;
                                                                errores.add(erroresS + "\n");
                                                                return;
                                                            }
                                                        } else {
                                                            errorS++;
                                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                                            errores.add(erroresS + "\n");
                                                            return;
                                                        }
                                                    } else {
                                                        errorS++;
                                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                                        errores.add(erroresS + "\n");
                                                        return;
                                                    }
                                                }
                                            } else {
                                                errorS++;
                                                erroresS = "Error de sintaxis en la linea: " + linea;
                                                errores.add(erroresS + "\n");
                                                return;
                                            }
                                        } else {
                                            errorS++;
                                            erroresS = "Error de sintaxis en la linea: " + linea;
                                            errores.add(erroresS + "\n");
                                            return;
                                        }
                                    } else {
                                        errorS++;
                                        erroresS = "Error de sintaxis en la linea: " + linea;
                                        errores.add(erroresS + "\n");
                                        return;
                                    }
                                } else {
                                    errorS++;
                                    erroresS = "Error de sintaxis en la linea: " + linea;
                                    errores.add(erroresS + "\n");
                                    return;
                                }
                            } else {
                                errorS++;
                                erroresS = "Error de sintaxis en la linea: " + linea;
                                errores.add(erroresS + "\n");
                                return;
                            }
                        } else {
                            errorS++;
                            erroresS = "Error de sintaxis en la linea: " + linea;
                            errores.add(erroresS + "\n");
                            return;
                        }
                    }
                } else {
                    errorS++;
                    erroresS = "Error de sintaxis en la linea: " + linea;
                    errores.add(erroresS + "\n");
                    return;
                }
            } else {
                errorS++;
                erroresS = "Error de sintaxis en la linea: " + linea;
                errores.add(erroresS + "\n");
                return;
            }

        } else {
            errorS++;
            erroresS = "Error de sintaxis en la linea: " + linea;
            errores.add(erroresS + "\n");
            return;
        }
    }

    void analisisSintactico() {
        linea++;
        if (esTipoEncapsulamiento(tokens.get(cont).getId())) {
            bloquebool = true;
            bloque();
        }
        if (esTipoDato(tokens.get(cont).getId())) {
            declaracion();
            linea++;
        }
        if (tokens.get(cont).getId() == 157) {
            forbool = true;
            para();
        }
        if (tokens.get(cont).getId() == 164) {
            sibool = true;
            si();
        }
        if (tokens.get(cont).getId() == 163) {
            mandarSalida();
        }
        if (tokens.get(cont).getId() == 156) {
            lectura();
        }
        if (tokens.get(cont).getId() >= 500) {
            operacion();
        }
    }

    boolean consultarFin() {
        if (incrementar()) {
            if (tokens.get(cont).getId() == 131) {
                declaracion();
            }
            if (tokens.get(cont).getId() == 134) {
                if (incrementar()) {
                    if (bloquebool) {
                        if (sibool)
                            analisisDelSi();
                        else
                            analisisDelBloque();
                    } else {
                        if (sibool)
                            analisisDelSi();
                        else
                            analisisSintactico();
                    }
                }
            }
            return true;
        }
        return false;
    }


    void analisisDelTipo() {
        int tipo = -1;
        do {
            if (tokens.get(contS).getId() >= 500) {
                if (tokens.get(contS).getTipo() == 0) {
                    if (tipo != -1)
                        tokens.get(contS).setTipo(tipo);
                    else {
                        for (int a = 0; a < tokens.size(); a++) {
                            if (tokens.get(a).getToken().equalsIgnoreCase(tokens.get(contS).getToken())) {
                                tokens.get(contS).setTipo(tokens.get(a).getTipo());
                                break;
                            }
                        }
                    }
                }
            }
            if (tokens.get(contS).getId() == 134 || tokens.get(contS).getId() == 127)
                tipo = -1;
            if (esTipoDato(tokens.get(contS).getId()))
                tipo = obtenerTipoDatos(tokens.get(contS).getId());
            if (esValor(tokens.get(contS).getId()))
                tokens.get(contS).setTipo(tokens.get(contS).getId());
        } while (incrementarS());
    }

    void analisisSemantico() {
        contS = 0;
        Semantico semantico = new Semantico();
        do {
            if (tokens.get(contS).getId() == 163) {
                incrementarS();
                incrementarS();
                if (tokens.get(contS).getTipo() != 182) {
                    erroresS = "Error Semantico: Datatype not match " +
                            semantico.getDataType(tokens.get(contS - 1).getTipo()) +
                            ":" + semantico.getDataType(182);
                    errores.add(erroresS + "\n");
                }
            }
            if (esComparativo(tokens.get(contS).getId())) {
                if (tokens.get(contS - 1).getTipo() == 0 && tokens.get(contS - 1).getId() >= 500) {
                    errorSema++;
                    erroresS = "Error Semantico: Unexpected " + tokens.get(contS - 1).getToken();
                    errores.add(erroresS + "\n");
                }
                if (tokens.get(contS + 1).getTipo() == 0 && tokens.get(contS + 1).getId() >= 500) {
                    errorSema++;
                    erroresS = "Error Semantico: Unexpected " + tokens.get(contS - 1).getToken();
                    errores.add(erroresS + "\n");
                }
                if (semantico.sameFamily(tokens.get(contS - 1), tokens.get(contS + 1))) {
                    incrementarS();
                } else {
                    errorSema++;
                    erroresS =
                            "Error Semantico: Datatype not match " +
                                    semantico.getDataType(tokens.get(contS - 1).getTipo()) +
                                    ":" + semantico.getDataType(tokens.get(contS + 1).getTipo());
                    errores.add(erroresS + "\n");
                }
            }
            if (tokens.get(contS).getId() == 175) {
                if (tokens.get(contS + 1).getTipo() == 0 && tokens.get(contS + 1).getId() >= 500) {
                    errorSema++;
                    erroresS = "Error Semantico: Unexpected " + tokens.get(contS - 1).getToken();
                    errores.add(erroresS + "\n");
                }
                if (tokens.get(contS - 1).getTipo() == 0 && tokens.get(contS - 1).getId() >= 500) {
                    errorSema++;
                    erroresS = "Error Semantico: Unexpected " + tokens.get(contS - 1).getToken();
                    errores.add(erroresS + "\n");
                } else {
                    int contA = contS - 1;
                    if (tokens.get(contA).getTipo() == 182) {
                        if (tokens.get(contS + 1).getTipo() != 182) {
                            errorSema++;
                            erroresS = "Error Semantico: Datatype not match " +
                                    semantico.getDataType(tokens.get(contA).getTipo()) +
                                    ":" + semantico.getDataType(tokens.get(contS).getTipo());
                            errores.add(erroresS + "\n");

                        }
                    } else {
                        String operacion = "";
                        boolean op = true;
                        while (incrementarS() && tokens.get(contS).getId() != 134 && tokens.get(contS).getId() != 131) {
                            operacion += tokens.get(contS).getToken();
                            if (tokens.get(contS).getId() >= 500)
                                op = false;
                            if (!esAritmetico(tokens.get(contS).getId())) {
                                if (semantico.isFloat(tokens.get(contA), tokens.get(contS))) {
                                    if (!semantico.sameFamily(tokens.get(contA), tokens.get(contS))) {
                                        op = false;
                                        errorSema++;
                                        erroresS =
                                                "Error Semantico: Datatype not match " +
                                                        semantico.getDataType(tokens.get(contA).getTipo()) +
                                                        ":" + semantico.getDataType(tokens.get(contS).getTipo());
                                        errores.add(erroresS + "\n");
                                    }
                                } else {
                                    if (tokens.get(contA).getTipo() == 180) {
                                        if (tokens.get(contA).getTipo() != tokens.get(contS).getTipo()) {
                                            op = false;
                                            errorSema++;
                                            erroresS =
                                                    "Error Semantico: Datatype not match " +
                                                            semantico.getDataType(tokens.get(contA).getTipo()) +
                                                            ":" + semantico.getDataType(tokens.get(contS).getTipo());
                                            errores.add(erroresS + "\n");
                                        }
                                    }
                                }
                            }
                        }
                        if (op)
                            System.out.println("Notación Polaca: " + operacion + " : " + convertir(operacion));
                    }
                }
            }
        } while (incrementarS());
    }

    int obtenerTipoDatos(int id) {
        int tipo = 0;
        switch (id) {
            case 155:
                tipo = 180;
                break;
            case 154:
                tipo = 181;
                break;
            case 151:
                tipo = 182;
                break;
        }
        return tipo;
    }

}
