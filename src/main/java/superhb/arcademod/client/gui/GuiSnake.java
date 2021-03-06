package superhb.arcademod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import superhb.arcademod.Reference;
import superhb.arcademod.api.gui.GuiArcade;
import superhb.arcademod.client.ArcadeItems;
import superhb.arcademod.client.tileentity.TileEntityArcade;
import superhb.arcademod.util.KeyHandler;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiSnake extends GuiArcade {
    private static final ResourceLocation texture = new ResourceLocation(Reference.MODID + ":textures/gui/snake.png");

    // Game Variables
    private int snakePosX = 0, snakePosY = 0; // 130x185 (26, 37)
    private int pointPosX = 0, pointPosY = 0;
    private int[] tailX = new int[200], tailY = new int[200];
    private int direction = 0, difficulty = 0; // direction == 0 = start, 1 = up, 2 = down, 3 = left, 4 = right; difficulty == 0 = easy (4), 1 = medium (3), 2 = hard (2), 3 = extreme (1)
    private int tail = 0, score = 0;
    private boolean start = true, gameOver = false;
    private int prevTick = 0, tick = 4;

    // Texture Variables
    private static final int GUI_X = 150;
    private static final int GUI_Y = 207;
    private static final int ARROW_X = 7;
    private static final int ARROW_Y = 11;
    private static final int SNAKE = 5;
    private static final int POINT = 3;

    public GuiSnake (World world, TileEntityArcade tileEntity, EntityPlayer player) {
        super(world, tileEntity, null, player);
        setGuiSize(GUI_X, GUI_Y);
        setButtonPos((GUI_X / 2) - (buttonWidth / 2), GUI_Y - 32);
        setTexture(texture);
        setStartMenu(0);
    }
    
    @Override
	public void updateScreen () {
    	super.updateScreen();
    	
    	if (inMenu) {
			if (menu == 3) {
				if ((tickCounter - prevTick) >= 60) {
					prevTick = tickCounter;
					checkMenuAfterGameOver();
					direction = 0;
					start = true;
					tail = 0;
				}
			}
		} else {
			if ((tickCounter - prevTick) >= tick) {
				prevTick = tickCounter;
			
				int preX = tailX[0];
				int preY = tailY[0];
				int pre2X, pre2Y;
			
				if (start) {
					pointPosX = getWorld().rand.nextInt(25);
					pointPosY = getWorld().rand.nextInt(36);
					start = false;
				}
			
				// Movement
				if (direction == 0) {
					snakePosX = (130 / 5) / 2;
					snakePosY = (185 / 5) / 2;
				} else if (direction == 1) { // Up
					tailX[0] = snakePosX;
					tailY[0] = snakePosY;
					snakePosY -= 1;
				} else if (direction == 2) { // Down
					tailX[0] = snakePosX;
					tailY[0] = snakePosY;
					snakePosY += 1;
				} else if (direction == 3) { // Left
					tailX[0] = snakePosX;
					tailY[0] = snakePosY;
					snakePosX -= 1;
				} else if (direction == 4) { // Right
					tailX[0] = snakePosX;
					tailY[0] = snakePosY;
					snakePosX += 1;
				}
			
				// Game Over
				if (snakePosX < 0 || snakePosX > 25 || snakePosY < 0 || snakePosY > 36) {
					direction = -1;
					gameOver = true;
				}
			
				for (int i = 0; i < tail; i++) {
					if (i < tailX.length) {
						if (tailX[i] == snakePosX && tailY[i] == snakePosY) {
							direction = -1;
							gameOver = true;
						}
					}
				}
			
				// Add Point
				if (snakePosX == pointPosX && snakePosY == pointPosY) {
					tail++;
					pointPosX = getWorld().rand.nextInt(25);
					pointPosY = getWorld().rand.nextInt(36);
					for (int i = 0; i < tail; i++) {
						if (i < tailX.length) {
							if ((pointPosX == tailX[i] && pointPosY == tailY[i]) || (pointPosX == snakePosX && pointPosY == snakePosY)) {
								pointPosX = getWorld().rand.nextInt(25);
								pointPosY = getWorld().rand.nextInt(36);
							}
						}
					}
				}
			
				// Tail
				for (int i = 1; i < tail; i++) {
					if (i < tailX.length) {
						pre2X = tailX[i];
						pre2Y = tailY[i];
						tailX[i] = preX;
						tailY[i] = preY;
						preX = pre2X;
						preY = pre2Y;
					}
				}
			}
		}
	}

    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        int minX = (width / 2) - (GUI_X / 2) + 10;
        int minY = (height / 2) - (GUI_Y / 2) + 10;

        super.drawScreen(mouseX, mouseY, partialTicks);

        // TODO: Make leaderboard menu
        // TODO: Save high score to nbt
        // TODO: Save leaderboard (Top 10) to nbt
		// TODO: Warn player when FPS is too low? (Minecraft.getDebugFPS())
        if (inMenu) {
			int startWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:start.locale"));
			int difficultyWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:difficulty.locale"));
			int controlWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:control.locale"));
	
			int easyWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:difficulty.easy.locale"));
			int mediumWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:difficulty.medium.locale"));
			int hardWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:difficulty.hard.locale"));
			int extremeWidth = this.fontRenderer.getStringWidth(I18n.format("option.arcademod:difficulty.extreme.locale"));
			
            switch (menu) {
                case 0: //Start Menu
                    int titleWidth = this.fontRenderer.getStringWidth(I18n.format("game.arcademod:snake.name"));
                    this.fontRenderer.drawString(I18n.format("game.arcademod:snake.name"), (width / 2) - (titleWidth / 2), (height / 2) - (GUI_Y / 2) + 11, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:start.locale"), (width / 2) - (startWidth / 2), height / 2, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.locale"), (width / 2) - (difficultyWidth / 2), height / 2 + 10, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:control.locale"), (width / 2) - (controlWidth / 2), height / 2 + 20, 16777215);

                    switch (menuOption) {
                        case 0: // Start
                            this.mc.getTextureManager().bindTexture(texture);
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                        case 1: // Option
                            this.mc.getTextureManager().bindTexture(texture);
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2 + 10, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                        case 2: // Controls
                            this.mc.getTextureManager().bindTexture(texture);
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2 + 20, 0, GUI_Y, ARROW_X, ARROW_Y);
                    }
                    break;
                case 1: // Options Menu
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.locale"), (width / 2) - (difficultyWidth / 2), (height / 2) - (GUI_Y / 2) + 11, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.easy.locale"), (width / 2) - (easyWidth / 2), height / 2, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.medium.locale"), (width / 2) - (mediumWidth / 2), height / 2 + 10, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.hard.locale"), (width / 2) - (hardWidth / 2), height / 2 + 20, 16777215);
                    this.fontRenderer.drawString(I18n.format("option.arcademod:difficulty.extreme.locale"), (width / 2) - (extremeWidth / 2), height / 2 + 30, 16777215);

                    this.mc.getTextureManager().bindTexture(texture);
                    switch (difficulty) {
                        case 0: // Easy
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                        case 1: // Medium
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2 + 10, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                        case 2: // Hard
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2 + 20, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                        case 3: // Extreme
                            this.drawTexturedModalRect(minX + 30, height / 2 - 2 + 30, 0, GUI_Y, ARROW_X, ARROW_Y);
                            break;
                    }
                    break;
                case 2: // Controls Menu
                    this.fontRenderer.drawString(I18n.format("option.arcademod:control.locale"), (width / 2) - (controlWidth / 2), (height / 2) - (GUI_Y / 2) + 11, Color.white.getRGB());

                    this.fontRenderer.drawString("[" + KeyHandler.up.getDisplayName() + "] " + I18n.format("control.arcademod:up.name"), (width / 2) - 40, (height / 2) - 10, Color.white.getRGB());
                    this.fontRenderer.drawString("[" + KeyHandler.down.getDisplayName() + "] " + I18n.format("control.arcademod:down.name"), (width / 2) - 40, height / 2, Color.white.getRGB());
                    this.fontRenderer.drawString("[" + KeyHandler.left.getDisplayName() + "] " + I18n.format("control.arcademod:left.name"), (width / 2) - 40, (height / 2) + 10, Color.white.getRGB());
                    this.fontRenderer.drawString("[" + KeyHandler.right.getDisplayName() + "] " + I18n.format("control.arcademod:right.name"), (width / 2) - 40, (height / 2) + 20, Color.white.getRGB());
                    this.fontRenderer.drawString("[" + KeyHandler.select.getDisplayName() + "] " + I18n.format("control.arcademod:select.name"), (width / 2) - 40, (height / 2) + 30, Color.white.getRGB());

                    this.fontRenderer.drawString("[" + KeyHandler.left.getDisplayName() + "] " + I18n.format("option.arcademod:back.name"), (width / 2) - (GUI_X / 2) + 12, (height / 2) + (GUI_Y / 2) - 20, Color.white.getRGB());
                    break;
                case 3: // Game Over Menu
                    int overWidth = this.fontRenderer.getStringWidth(I18n.format("text.arcademod:gameover.locale"));
                    this.fontRenderer.drawString(I18n.format("text.arcademod:gameover.locale"), (width / 2) - (overWidth / 2), (height / 2) - 20, Color.white.getRGB());
                    int scoreWidth = this.fontRenderer.getStringWidth(I18n.format("text.arcademod:score.locale") + ": " + tail);
                    this.fontRenderer.drawString(I18n.format("text.arcademod:score.locale") + ": " + tail, (width / 2) - (scoreWidth / 2), (height / 2) - 10, Color.white.getRGB());
                    // TODO: Show Highscore
                    break;
                case 4: // ArcadeLeaderboard Menu
                    break;
            }
        } else {
            if (gameOver) {
                menu = 3;
                score = tail;
                inMenu = true;
                gameOver = false;
                giveReward(ArcadeItems.TICKET, (score / 10));
                // TODO: Send Score to NBT
            }

            // Snake
            this.drawTexturedModalRect(minX + (snakePosX * 5), minY + (snakePosY * 5), ARROW_X, GUI_Y, SNAKE, SNAKE);

            // Point
            this.drawTexturedModalRect(minX + 1 + (pointPosX * 5), minY + 1 + (pointPosY * 5), ARROW_X + SNAKE, GUI_Y, POINT, POINT);

            // Tail
            for (int i = 1; i <= tail; i++) {
                if (i < tailX.length) this.drawTexturedModalRect(minX + (tailX[i - 1] * 5), minY + (tailY[i - 1] * 5), ARROW_X, GUI_Y, SNAKE, SNAKE);
            }

            // Score
            int scoreWidth = this.fontRenderer.getStringWidth(I18n.format("text.arcademod:score.locale") + ": " + tail);
            this.fontRenderer.drawString(I18n.format("text.arcademod:score.locale") + ": " + tail, (width / 2) - (scoreWidth / 2), (height / 2) + 93, 4210752);
        }
    }

    // TODO: Add Pause button and menu
    @Override
    protected void keyTyped (char typedChar, int keyCode) throws IOException {
        if (keyCode == KeyHandler.up.getKeyCode()) { // Up arrow
            if (inMenu) {
                if (menu == 0) {
                    if (menuOption == 0) menuOption = 2;
                    else menuOption -= 1;
                } else if (menu == 1) {
                    if (difficulty == 0) difficulty = 3;
                    else difficulty -= 1;
                }
            } else {
                if (tail > 0) {
                    if (direction != 2) direction = 1;
                    else direction = 2;
                } else {
                    direction = 1;
                }
            }
        }
        if (keyCode == KeyHandler.down.getKeyCode()) { // Down arrow
            if (inMenu) {
                if (menu == 0) {
                    if (menuOption == 2) menuOption = 0;
                    else menuOption += 1;
                } else if (menu == 1) {
                    if (difficulty == 3) difficulty = 0;
                    else difficulty += 1;
                }
            } else {
                if (tail > 0) {
                    if (direction != 1) direction = 2;
                    else direction = 1;
                } else {
                    direction = 2;
                }
            }
        }
        if (keyCode == KeyHandler.left.getKeyCode()) { // Left arrow
            if (inMenu) {
                if (menu == 2) menu = 0;
            } else {
                if (tail > 0) {
                    if (direction != 4) direction = 3;
                    else direction = 4;
                } else {
                    direction = 3;
                }
            }
        }
        if (keyCode == KeyHandler.right.getKeyCode()) { // Right arrow
            if (!inMenu) {
                if (tail > 0) {
                    if (direction != 3) direction = 4;
                    else direction = 3;
                } else {
                    direction = 4;
                }
            }
        }
        if (keyCode == KeyHandler.select.getKeyCode()) { // Enter
            if (inMenu) {
                if (menu == 0) {
                    switch (menuOption) {
                        case 0: // Start
                            inMenu = false;
                            canGetCoinBack = false;
                            break;
                        case 1: // Options
                            menu = 1;
                            break;
                        case 2: // Controls
                            menu = 2;
                            break;
                    }
                } else if (menu == 1) {
                    switch (difficulty) {
                        case 0: // Easy
                            tick = 4;
                            break;
                        case 1: // Medium
                            tick = 3;
                            break;
                        case 2: // Hard
                            tick = 2;
                            break;
                        case 3: // Extreme
                            tick = 1;
                            break;
                    }
                    menu = 0;
                }
            }
        }
        if (keyCode == 1) { // Esc
            if (!inMenu) giveReward(ArcadeItems.TICKET, (score / 10));
        }
        super.keyTyped(typedChar, keyCode);
    }
}
