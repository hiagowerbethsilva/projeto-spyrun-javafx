package br.com.spyrun;

import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * Classe Enemy
 * Representa um inimigo do jogo, responsável por
 * perseguir o jogador e atirar periodicamente.
 */
public class Enemy implements Movable {

    // Posição atual do inimigo na tela
    public double x;
    public double y;

    // Velocidade de movimento do inimigo (pixels por segundo, por exemplo)
    public double speed = 80;

    // Indica se o inimigo ainda está vivo/ativo no jogo
    public boolean alive = true;

    // Vida atual do inimigo (0 a 100)
    public double health = 100;

    // Intervalo de tempo mínimo entre disparos (em segundos)
    public double shootCooldown = 1.0; // 1 segundo

    // Tempo acumulado desde o último disparo
    public double timeSinceShot = 0;

    // Direção atual do inimigo para selecionar o sprite
    public Player.Direction direction = Player.Direction.DOWN;

    // Sprites do inimigo
    private Image spriteUp;
    private Image spriteDown;
    private Image spriteLeft;
    private Image spriteRight;
    private Image defaultSprite;


    /**
     * Construtor do inimigo.
     * @param x posição inicial X
     * @param y posição inicial Y
     */
    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    /**
     * Carrega as imagens (sprites) do inimigo a partir da pasta de resources.
     */
    private void loadSprites() {
        try {
            defaultSprite = new Image(getClass().getResourceAsStream("/sprites/Enemy-down.png"));
        } catch (Exception e) {
            System.err.println("ERRO: Não foi possível carregar o sprite padrão Enemy.png");
            defaultSprite = null;
        }
        try {
            spriteUp = new Image(getClass().getResourceAsStream("/sprites/Enemy-up.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Enemy-up.png");
            spriteUp = null;
        }
        try {
            spriteDown = new Image(getClass().getResourceAsStream("/sprites/Enemy-down.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Enemy-down.png");
            spriteDown = null;
        }
        try {
            spriteLeft = new Image(getClass().getResourceAsStream("/sprites/Enemy-left.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Enemy-left.png");
            spriteLeft = null;
        }
        try {
            spriteRight = new Image(getClass().getResourceAsStream("/sprites/Enemy-right.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Enemy-right.png");
            spriteRight = null;
        }
    }

    /**
     * Retorna a imagem correspondente à direção atual do inimigo.
     * @return A imagem do sprite a ser desenhada.
     */
    public Image getImage() {
        switch (direction) {
            case UP:
                return spriteUp != null ? spriteUp : defaultSprite;
            case DOWN:
                return spriteDown != null ? spriteDown : defaultSprite;
            case LEFT:
                return spriteLeft != null ? spriteLeft : defaultSprite;
            case RIGHT:
                return spriteRight != null ? spriteRight : defaultSprite;
            default:
                return defaultSprite;
        }
    }

    /**
     * Atualiza o inimigo a cada frame.
     * - Move o inimigo em direção ao jogador.
     * - Atualiza o tempo desde o último disparo.
     *
     * @param delta  tempo decorrido desde o último frame (em segundos)
     * @param player referência ao jogador, usada para seguir sua posição
     */
    public void update(double delta, Player player) {
        // A lógica de movimento foi movida para a classe Main.
        // Este método agora apenas atualiza o cooldown do tiro.

        // Incrementa o tempo desde o último disparo
        timeSinceShot += delta;
    }

    /**
     * Verifica se o inimigo está pronto para atirar.
     * @return true se já passou tempo suficiente desde o último tiro.
     *         Neste caso, o contador é reiniciado.
     */
    public boolean readyToShoot() {
        if (timeSinceShot >= shootCooldown) {
            timeSinceShot = 0; // reinicia o tempo após o disparo
            return true;
        }
        return false;
    }

    /**
     * Aplica dano ao inimigo.
     * @param amount quantidade de dano recebido
     * @return true se a vida chegou a 0 ou menos (inimigo morreu)
     */
    public boolean takeDamage(double amount) {
        health -= amount;
        return health <= 0;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - 15, y - 15, 30, 30);
    }
}
