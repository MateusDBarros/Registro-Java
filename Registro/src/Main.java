import java.sql.*;
import java.util.*;



public class Main {

    //Função para adicionar novos registros no banco de dados
    public static void insertRegister(Connection conn, Registro registro) throws SQLException {
        String sql = "Insert into registro (name, age, userid) values (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, registro.getName());
            stmt.setInt(2, registro.getAge());
            stmt.setInt(3, registro.getId() + 1);
            stmt.executeUpdate();
        }
    }

    //Função para apagar um registro
    public static void deleteRegister(Connection conn, String name) throws SQLException {
        String sql = "delete from registro where name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
    //Função para recuperar os dados
    public static ArrayList<Registro> getRegister(Connection conn) throws SQLException {
        String sql = "select name, age, userid from registro";
        ArrayList<Registro> registros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int id = rs.getInt("userid");
                registros.add(new Registro(name, age, id));
            }
        }
        return registros;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Registro> registros = new ArrayList<>();


        String url = "jdbc:postgresql://localhost:5432/RegistroDB";
        String user = "postgres";
        String password = "lilY@";

        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url,user,password);
        System.out.println("Conexão concluida\nIniciando o Programa...\n\n");

        registros.clear(); //Limpando a lista antes de  recuperar os dados


        registros = getRegister(conn);  //Recuperando os dados

        Collections.sort(registros, new Comparator<Registro>() {
            @Override
            public int compare(Registro o1, Registro o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });

        int escolha;
        do {
            System.out.println("1. Criar Cadastro.");
            System.out.println("2. Exibir Cadastros.");
            System.out.println("3. Pesquisar Cadastro.");
            System.out.println("4. Deletar Cadastro.");
            System.out.println("5. Sair.");
            escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    Registro newRegistro = Registro.create(registros);
                    insertRegister(conn, newRegistro);
                    registros.add(newRegistro);
                    registros = getRegister(conn);
                    break;
                case 2:
                    Registro.list(registros);
                    System.out.println();
                    break;
                case 3:
                    System.out.println("Função a ser implementada");
                    break;

                case 4:
                    String name = Registro.delete(conn, registros);
                    deleteRegister(conn, name);
                    registros = getRegister(conn);
                    break;

                case 5:
                    System.out.println("\t---Software made by mateusDbarros");
                    return;

                default:
                    System.out.println("Opção invalida!");
                    break;
            }


        } while (5 != escolha);
    }


}
class Registro {
    private String name;
    private int age;
    private int id;


    public Registro (String name, int age, int id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public int getId(){
        return id;
    }

    private static Scanner scanner = new Scanner(System.in);

    public static Registro create(ArrayList<Registro> registros) {
        System.out.print("Digite um nome para cadastro: ");
        String name = scanner.nextLine();
        System.out.print("Digite a idade de " + name + ": ");
        int age = scanner.nextInt();
        scanner.nextLine();
        int id = registros.size();
        Registro newRegistro =  new Registro(name, age, id);
        System.out.println("Cadastro Registrado com sucesso!");
        return newRegistro;
    }

    public static void list(ArrayList<Registro> registros) {
        if (!registros.isEmpty()) {

            System.out.printf("%-5s | %-15s | %-9s\n", "ID", "Nome", "Idade");
            System.out.println("-----------------------------------");

            for (int i = 0; i < registros.size(); i++) {
                System.out.printf("%-5d | %-15s | %-9d\n", registros.get(i).getId(), registros.get(i).getName(), registros.get(i).getAge());
            }
        } else
            System.out.println("Sem registros no banco de dados");

    }


    public static String delete(Connection conn, ArrayList<Registro> registros) throws SQLException {
        if (registros.isEmpty()) {
            return "Nenhum cadastro registrado!";
        }

        String input;
        char inputchar;
        System.out.printf("Digite o nome do usuario que deseja excluir (digite 'sair' para voltar ao menu anterior): ");
        input = scanner.nextLine();

        if (input.equals("sair"))
            return "Saindo...";

        for (int i = 0; i < registros.size(); i++) {
            if (input.equals(registros.get(i).getName())) {
                System.out.printf("Deseja mesmo excluir esse Cadastro (y/n)? ");
                inputchar = scanner.next().charAt(0);
                scanner.nextLine();
                if (inputchar == 'n')
                    return "Saindo...";

                else if (inputchar == 'y') {
                    registros.remove(i);
                    System.out.println("Cadastro excluido com sucesso!");
                    return input;
                }
                else {
                    return "Cadastro não encontrado";
                }
            }
        }
        return input;
    }
}