package br.com.spyrun;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Classe principal do jogo.
 * Responsável por inicializar a janela, configurar controles,
 * gerenciar o loop de jogo (update/desenho) e controlar player,
 * inimigos, balas e câmera.
 */
public class Main extends Application {

    // Dimensões da área visível (janela)
    private static final int VIEW_WIDTH = 960;
    private static final int VIEW_HEIGHT = 640;

    // Dimensões do mundo lógico (maior que a tela)
    private static final int WORLD_WIDTH = 2000;
    private static final int WORLD_HEIGHT = 2000;

    // Intervalo em segundos para gerar novos inimigos
    private static final double ENEMY_SPAWN_INTERVAL = 5.0;

    private Canvas canvas;
    private GraphicsContext g;

    // Referência ao jogador
    private Player player;

    // Flags de controle de movimento
    private boolean up, down, left, right;

    // Coordenadas do mouse na tela
    private double mouseX, mouseY;

    // Flag de disparo (um tiro por clique)
    private boolean shooting;

    // Listas de balas, inimigos e corações ativos
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<MapObject> mapObjects = new ArrayList<>();
    private final List<Coracao> coracoes = new ArrayList<>();

    // Sprites
    private Image imagemCoracao;

    // Controle de tempo entre frames
    private long lastTime = 0;

    // Indica se o jogo terminou
    private boolean gameOver = false;

    // Temporizadores para spawn de novos inimigos e corações
    private double enemySpawnTimer = 0;
    private double temporizadorSpawnCoracao = 0;

    private final Random random = new Random();

    // Posição da câmera no mundo
    private double cameraX, cameraY;
    private OfficeMap officeMap;


    @Override
    public void start(Stage stage) {
        // Cria o canvas de desenho com o tamanho da janela
        canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        g = canvas.getGraphicsContext2D();
        g.setImageSmoothing(false);

        // Carrega a imagem do coração
        try {
            imagemCoracao = new Image(getClass().getResourceAsStream("/sprites/heart.svg"), 32, 32, true, true);
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o sprite do coração.");
            imagemCoracao = null;
        }

        // Instancia o jogador no centro do mundo
        player = new Player(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0);

        // Cria alguns inimigos iniciais em posições diferentes
        enemies.add(new Enemy(400, 400));
        enemies.add(new Enemy(1600, 1600));
        enemies.add(new Enemy(1600, 400));

        // Gera alguns corações iniciais
        for (int i = 0; i < 3; i++) {
            gerarCoracao();
        }

        // Adiciona objetos de colisão (paredes, mesas, etc.)
        // Paredes

        // Mesas
        mapObjects.add(new MapObject(400, 400, 150, 80, Color.web("8B4513")));
        mapObjects.add(new MapObject(800, 600, 150, 80, Color.web("8B4513")));
        mapObjects.add(new MapObject(1200, 400, 150, 80, Color.web("8B4513")));
        mapObjects.add(new MapObject(400, 1000, 150, 80, Color.web("8B4513")));
        mapObjects.add(new MapObject(800, 1200, 150, 80, Color.web("8B4513")));
        mapObjects.add(new MapObject(1200, 1000, 150, 80, Color.web("8B4513")));
        
        officeMap = new OfficeMap(mapObjects);

        // Monta a cena com o canvas como raiz
        StackPane root = new StackPane(canvas);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Configuração dos controles de teclado e mouse
        configurarControles(scene);

        stage.setScene(scene);
        stage.setTitle("SpyRun - Protótipo Shooter");
        stage.show();

        // Inicia o loop principal do jogo
        iniciarLoop();
    }

    /**
     * Configura as entradas de teclado (WASD) e mouse (clique para atirar).
     */
    private void configurarControles(Scene scene) {

        // Pressionar tecla: ativa a direção correspondente
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) { up = true; player.direction = Player.Direction.UP; }
            if (e.getCode() == KeyCode.S) { down = true; player.direction = Player.Direction.DOWN; }
            if (e.getCode() == KeyCode.A) { left = true; player.direction = Player.Direction.LEFT; }
            if (e.getCode() == KeyCode.D) { right = true; player.direction = Player.Direction.RIGHT; }
        });
    
        // Soltar tecla: desativa a direção correspondente
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) up = false;
            if (e.getCode() == KeyCode.S) down = false;
            if (e.getCode() == KeyCode.A) left = false;
            if (e.getCode() == KeyCode.D) right = false;
        }); 

        // Disparo do jogador: um tiro por clique do mouse esquerdo
        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !gameOver) {
                shooting = true;
                atirarPlayerEmDirecaoAoMouse();
            }
        });

        // Quando soltar o botão do mouse, para de considerar o disparo
        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shooting = false;
            }
        });

        // Atualiza a posição do mouse sempre que ele se mover
        scene.setOnMouseMoved(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
    }

    /**
     * Cria e inicia o AnimationTimer, responsável por chamar
     * atualizar() e desenhar() a cada frame.
     */
    private void iniciarLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Inicialização do lastTime
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                // Cálculo de delta (segundos entre frames)
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                atualizar(delta);
                desenhar();
            }
        };
        timer.start();
    }

    private void checkAndApplyEntityMovement(Movable entity, double dx, double dy) {
        double oldX = entity.getX();
        double oldY = entity.getY();

        entity.setX(oldX + dx);
        entity.setY(oldY + dy);

        for (MapObject obj : mapObjects) {
            Rectangle objBounds = obj.getBounds();
            if (entity.getBounds().intersects(objBounds.getX(), objBounds.getY(), objBounds.getWidth(), objBounds.getHeight())) {
                entity.setX(oldX);
                entity.setY(oldY);
                break;
            }
        }
    }

    /**
     * Atualiza o estado do jogo:
     * - movimenta o jogador
     * - atualiza inimigos e disparos
     * - verifica colisões
     * - gerencia spawn de novos inimigos
     */
    private void atualizar(double delta) {
        if (gameOver) {
            // Se o jogo acabou, não atualiza mais lógica
            return;
        }

        // Movimentação do jogador
        double dx = 0, dy = 0;
        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            dx = (dx / len) * player.speed * delta;
            dy = (dy / len) * player.speed * delta;
            checkAndApplyEntityMovement(player, dx, dy);
        }

        // Mantém o jogador dentro dos limites do mundo
        player.x = Math.max(0, Math.min(WORLD_WIDTH, player.x));
        player.y = Math.max(0, Math.min(WORLD_HEIGHT, player.y));

        // Atualiza a posição da câmera para centralizar o player
        cameraX = player.x - VIEW_WIDTH / 2.0;
        cameraY = player.y - VIEW_HEIGHT / 2.0;

        // Limita a câmera para não mostrar fora do mundo
        cameraX = Math.max(0, Math.min(WORLD_WIDTH - VIEW_WIDTH, cameraX));
        cameraY = Math.max(0, Math.min(WORLD_HEIGHT - VIEW_HEIGHT, cameraY));

        // Atualiza cada inimigo: movimento + tiro automático
        for (Enemy enemy : enemies) {
            if (enemy.alive) {
                // Calcula a direção do movimento do inimigo
                double enemyDX = player.x - enemy.x;
                double enemyDY = player.y - enemy.y;
                double enemyLen = Math.sqrt(enemyDX * enemyDX + enemyDY * enemyDY);

                if (enemyLen > 0) {
                    // Define a direção do sprite do inimigo
                    if (Math.abs(enemyDX) > Math.abs(enemyDY)) {
                        enemy.direction = (enemyDX > 0) ? Player.Direction.RIGHT : Player.Direction.LEFT;
                    } else {
                        enemy.direction = (enemyDY > 0) ? Player.Direction.DOWN : Player.Direction.UP;
                    }

                    enemyDX = (enemyDX / enemyLen) * enemy.speed * delta;
                    enemyDY = (enemyDY / enemyLen) * enemy.speed * delta;
                    checkAndApplyEntityMovement(enemy, enemyDX, enemyDY);
                }

                enemy.update(delta, player);
                if (enemy.alive && enemy.readyToShoot()) {
                    atirarInimigo(enemy);
                }
            }
        }

        // Atualiza posição das balas ativas
        for (Bullet b : bullets) {
            if (b.alive) {
                b.update(delta);
            }
        }

        // Verifica colisões entre balas, inimigos e jogador
        checarColisoes();

        // Remove balas e inimigos mortos/inativos das listas
        bullets.removeIf(b -> !b.alive);
        enemies.removeIf(e -> !e.alive);

        // Atualiza temporizador de spawn de inimigos
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= ENEMY_SPAWN_INTERVAL) {
            enemySpawnTimer = 0;
            spawnEnemy();
        }

        // Atualiza temporizador de spawn de corações
        temporizadorSpawnCoracao += delta;
        if (temporizadorSpawnCoracao >= 10.0) { // A cada 10 segundos
            temporizadorSpawnCoracao = 0;
            gerarCoracao();
        }
    }

    /**
     * Verifica colisões entre:
     * - balas do player e inimigos
     * - balas dos inimigos e o jogador
     * - jogador e corações de vida
     * Aplica dano e trata morte / fim de jogo.
     */
    private void checarColisoes() {
        for (Bullet b : bullets) {
            if (!b.alive) continue;

            if (b.owner == Shooter.PLAYER) {
                // Colisão da bala do player com cada inimigo
                for (Enemy e : enemies) {
                    if (!e.alive) continue;
                    double dist2 = (b.x - e.x) * (b.x - e.x) + (b.y - e.y) * (b.y - e.y);
                    // Raio de colisão aproximado (20 pixels)
                    if (dist2 < 20 * 20) {
                        b.alive = false;
                        boolean morto = e.takeDamage(b.damage);
                        if (morto) {
                            e.alive = false;
                        }
                    }
                }
            } else if (b.owner == Shooter.ENEMY) {
                // Colisão da bala do inimigo com o jogador
                double dist2 = (b.x - player.x) * (b.x - player.x) + (b.y - player.y) * (b.y - player.y);
                // Raio de colisão menor para o player (15 pixels)
                if (dist2 < 15 * 15) {
                    b.alive = false;
                    boolean morto = player.takeDamage(b.damage);
                    if (morto) {
                        gameOver = true;
                    }
                }
            }
        }

        // Colisão com corações de vida
        Iterator<Coracao> coracaoIterator = coracoes.iterator();
        while (coracaoIterator.hasNext()) {
            Coracao coracao = coracaoIterator.next();
            Rectangle coracaoBounds = coracao.getBounds();
            if (player.getBounds().intersects(coracaoBounds.getX(), coracaoBounds.getY(), coracaoBounds.getWidth(), coracaoBounds.getHeight())) {
                if (player.health < Player.VIDA_MAXIMA) {
                    player.curar();
                }
                coracaoIterator.remove(); // Remove o coração de forma segura
            }
        }
    }

    /**
     * Cria uma bala a partir da posição do player em direção ao cursor do mouse.
     */
    private void atirarPlayerEmDirecaoAoMouse() {
        // Converte a posição do mouse (coordenadas de tela) para o mundo
        double mouseWorldX = mouseX + cameraX;
        double mouseWorldY = mouseY + cameraY;

        // Calcula a direção do player para o mouse
        double dirX = mouseWorldX - player.x;
        double dirY = mouseWorldY - player.y;

        // Cria a bala
        Bullet b = new Bullet(player.x, player.y, dirX, dirY, Shooter.PLAYER);
        if (b.alive) {
            bullets.add(b);
        }
    }

    /**
     * Cria uma bala a partir de um inimigo em direção ao player.
     */
    private void atirarInimigo(Enemy enemy) {
        double dirX = player.x - enemy.x;
        double dirY = player.y - enemy.y;
        Bullet b = new Bullet(enemy.x, enemy.y, dirX, dirY, Shooter.ENEMY);
        if (b.alive) {
            bullets.add(b);
        }
    }

    /**
     * Gera um novo inimigo em uma posição aleatória do mundo.
     */
    private void spawnEnemy() {
        double x = random.nextDouble() * WORLD_WIDTH;
        double y = random.nextDouble() * WORLD_HEIGHT;
        enemies.add(new Enemy(x, y));
    }

    /**
     * Gera um novo coração em uma posição aleatória do mundo.
     */
    private void gerarCoracao() {
        if (imagemCoracao != null) {
            double x = random.nextDouble() * WORLD_WIDTH;
            double y = random.nextDouble() * WORLD_HEIGHT;
            coracoes.add(new Coracao(x, y, imagemCoracao));
        }
    }

    /**
     * Responsável por desenhar todo o estado do jogo na tela:
     * fundo, grade, player, inimigos, balas, HUD e tela de game over.
     */
    private void desenhar() {
        // Preenche o fundo com um cinza para o chão do escritório
        g.setFill(Color.rgb(180, 180, 180));
        g.fillRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
    
        // Desenha os objetos do mapa (paredes, mesas)
        officeMap.draw(g, cameraX, cameraY);
        // Converte posição do player para coordenadas de tela
        double playerScreenX = player.x - cameraX;
        double playerScreenY = player.y - cameraY;

        // Desenha o sprite do player
        Image playerImage = player.getImage();
        if (playerImage != null) {
            double w = 32;
            double h = 32;
            g.drawImage(playerImage, playerScreenX - w / 2, playerScreenY - h / 2, w, h);
        } else {
            // Desenha um círculo azul se o sprite não puder ser carregado
            g.setFill(Color.DARKBLUE);
            g.fillOval(playerScreenX - 15, playerScreenY - 15, 30, 30);
        }

        // Desenha inimigos e suas barras de vida
        for (Enemy e : enemies) {
            double ex = e.x - cameraX;
            double ey = e.y - cameraY;

            // Desenha o sprite do inimigo
            Image enemyImage = e.getImage();
            if (enemyImage != null) {
                double w = 32;
                double h = 32;
                g.drawImage(enemyImage, ex - w / 2, ey - h / 2, w, h);
            } else {
                // Fallback para desenhar um retângulo se o sprite não carregar
                g.setFill(Color.CRIMSON);
                g.fillRect(ex - 15, ey - 15, 30, 30);
            }

            // Barra de vida (fundo branco)
            g.setFill(Color.WHITE);
            g.fillRect(ex - 15, ey - 25, 30, 4);

            // Parte vermelha proporcional à vida atual
            g.setFill(Color.RED);
            double lifeWidth = Math.max(0, Math.min(30, (e.health / 100.0) * 30));
            g.fillRect(ex - 15, ey - 25, lifeWidth, 4);
        }

        // Desenha as balas em verde
        g.setFill(Color.LIME);
        for (Bullet b : bullets) {
            double bx = b.x - cameraX;
            double by = b.y - cameraY;
            g.fillOval(bx - 4, by - 4, 8, 8);
        }

        // Desenha os corações de vida no mapa
        for (Coracao coracao : coracoes) {
            coracao.draw(g, cameraX, cameraY);
        }

        // HUD: exibe vida do jogador como corações
        if (imagemCoracao != null) {
            for (int i = 0; i < player.health; i++) {
                g.drawImage(imagemCoracao, 10 + (i * 35), 10, 32, 32);
            }
        } else {
            // Fallback se a imagem do coração não carregar
            g.setFill(Color.WHITE);
            g.fillText("Vida: " + player.health, 10, 20);
        }

        // Tela de Game Over
        if (gameOver) {
            g.setFill(Color.BLACK);
            g.fillRect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
            g.setFill(Color.WHITE);
            g.fillText("GAME OVER", VIEW_WIDTH / 2.0 - 40, VIEW_HEIGHT / 2.0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

