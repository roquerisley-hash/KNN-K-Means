import java.util.List;
import java.util.Optional;

interface JogadorDAO {
    public List<Jogador> listarTodos();
    public Optional<Jogador> buscarPorId(int id);
    public List<Jogador> listarPorPerfil(Perfil perfil);
}
