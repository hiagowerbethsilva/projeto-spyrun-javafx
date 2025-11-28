package br.com.spyrun;

/**
 * Classe Bullet
 * Representa um projétil disparado por um atirador (player ou inimigo).
 * Armazena posição, velocidade, dano e referência de quem disparou.
 */
public class Bullet {

    // Coordenadas atuais da bala na tela
    public double x;
    public double y;

    // Componentes de velocidade da bala em cada eixo
    public double vx;
    public double vy;

    // Velocidade escalar da bala (pixels por segundo, por exemplo)
    public double speed = 400;

    // Quantidade de dano que este projétil causa ao atingir um alvo
    public double damage = 20;

    // Indica se a bala ainda está ativa no jogo (true) ou deve ser removida (false)
    public boolean alive = true;

    // Dono do disparo: PLAYER (jogador) ou ENEMY (inimigo)
    public Shooter owner;

    /**
     * Construtor da bala.
     * @param x posição inicial X
     * @param y posição inicial Y
     * @param dirX componente X da direção do disparo
     * @param dirY componente Y da direção do disparo
     * @param owner dono do tiro (PLAYER ou ENEMY)
     */
    public Bullet(double x, double y, double dirX, double dirY, Shooter owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;

        // Calcula o comprimento (módulo) do vetor direção
        double len = Math.sqrt(dirX * dirX + dirY * dirY);

        // Se o vetor direção for muito pequeno, evita divisão por zero
        if (len < 1e-6) {
            alive = false; // bala inválida, marcada como morta
            vx = 0;
            return;
        }

        // Normaliza o vetor direção e multiplica pela velocidade desejada
        this.vx = (dirX / len) * speed;
        this.vy = (dirY / len) * speed;
    }

    /**
     * Atualiza a posição da bala ao longo do tempo.
     * @param delta tempo decorrido desde o último frame (em segundos)
     */
    public void update(double delta) {
        x += vx * delta;
        y += vy * delta;
    }
}
