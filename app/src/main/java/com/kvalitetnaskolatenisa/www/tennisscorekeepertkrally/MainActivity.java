package com.kvalitetnaskolatenisa.www.tennisscorekeepertkrally;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    int pointsPlayer1 = 0;
    int pointsPlayer2 = 0;
    int gamesPlayer1 = 0;
    int gamesPlayer2 = 0;
    int setsPlayer1 = 0;
    int setsPlayer2 = 0;

    int numberOfSetsForWin = 2;
    int numberOfServeInTieBreak = 0;
    boolean serveOfPlayer = true;
    boolean serveOfPlayerInTieBreak = true;
    boolean tieBreak = false;
    boolean firstFault = true;
    boolean showStatistic = true;
    boolean matchWon = false;
    boolean tiebreakFinal = true;
    /**
     * statistic variables
     */
    int winnerPlayer1 = 0;
    int acePlayer1 = 0;
    int faultPlayer1 = 0;
    int doubleFaultPlayer1 = 0;
    int forcedErrorPlayer1 = 0;
    int unforcedErrorPlayer1 = 0;
    int winnerPlayer2 = 0;
    int acePlayer2 = 0;
    int faultPlayer2 = 0;
    int doubleFaultPlayer2 = 0;
    int forcedErrorPlayer2 = 0;
    int unforcedErrorPlayer2 = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * manage preferences of app. Tiebreak and number of sets for win
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String setsNumber = sharedPrefs.getString(
                getString(R.string.settings_number_of_sets_key),
                getString(R.string.settings_number_of_sets_default));
        String tiebreakFinalString = sharedPrefs.getString(
                getString(R.string.settings_tiebreak_key),
                getString(R.string.settings_tiebreak_default));
        numberOfSetsForWin = Integer.parseInt(setsNumber);
        TextView textViewRules = (TextView) findViewById(R.id.rules);
        textViewRules.setText("Number of sets for win: " + setsNumber + "  Play tiebreak: " + tiebreakFinalString);
        if (tiebreakFinalString.equals("No")) {
            tiebreakFinal = false;
        } else {
            tiebreakFinal = true;
        }
        /**
         * hide buttons and statistic at onCreate
         */
        Button buttonPlayer2Ace = (Button) findViewById(R.id.button_player2_ace);
        Button buttonPlayer2Fault = (Button) findViewById(R.id.button_player2_fault);
        LinearLayout linearLayoutStatistic = (LinearLayout) findViewById(R.id.layout_statistic);
        buttonPlayer2Ace.setVisibility(View.INVISIBLE);
        buttonPlayer2Fault.setVisibility(View.INVISIBLE);
        linearLayoutStatistic.setVisibility(View.GONE);
    }

    /**
     * Displays the given points for Player 1.
     * manage tiebreak
     */
    public void displayPointsForPlayer1(int points) {
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        textViewDeuce.setText("");
        if (points <= 40 || tieBreak) {
            scoreViewPlayer1.setText(String.valueOf(points));
            if (pointsPlayer1 == 40 && pointsPlayer2 == 40 && !tieBreak) {
                textViewDeuce.setText(getString(R.string.deuce));
            }
            /**
             * manage tiebreak service
             */
            if (tieBreak) {
                numberOfServeInTieBreak++;
                if (pointsPlayer1 + pointsPlayer2 == 1) {
                    serveOfPlayerInTieBreak = !serveOfPlayerInTieBreak;
                    numberOfServeInTieBreak = 0;
                }
                if (numberOfServeInTieBreak > 1) {
                    serveOfPlayerInTieBreak = !serveOfPlayerInTieBreak;
                    numberOfServeInTieBreak = 0;
                }
                serveChange(serveOfPlayerInTieBreak);
            }
            if (!serveOfPlayer && pointsPlayer1 == 40 && !tieBreak && pointsPlayer2 < 40) {
                textViewDeuce.setText(getString(R.string.break_point_player1));
            }
            /**
             * check if player has Win the tiebreak
             */
            if (pointsPlayer1 >= 6 && pointsPlayer1 > pointsPlayer2 + 1 && tieBreak) {
                gamesPlayer1++;
                displayGamesForPlayer1(gamesPlayer1);
                tieBreak = false;

            }
            return;
        }
        if (points == pointsPlayer2) {
            scoreViewPlayer1.setText(getString(R.string.points_40));
            scoreViewPlayer2.setText(getString(R.string.points_40));
            textViewDeuce.setText(getString(R.string.deuce) + " (" + (pointsPlayer1 - 39) + ")");
            return;
        }
        if (points == pointsPlayer2 + 1) {
            scoreViewPlayer1.setText(getString(R.string.advance));
            scoreViewPlayer2.setText("");
            if (!serveOfPlayer) {
                textViewDeuce.setText(getString(R.string.break_point_player1));
            }
            return;
        }
        if (points == pointsPlayer2 + 2) {
            gamesPlayer1++;
            displayGamesForPlayer1(gamesPlayer1);
        }
    }

    /**
     * Displays the given points for Player 2.
     * manage tiebreak
     */
    public void displayPointsForPlayer2(int points) {
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        textViewDeuce.setText("");
        if (points <= 40 || tieBreak) {
            scoreViewPlayer2.setText(String.valueOf(points));
            if (pointsPlayer1 == 40 && pointsPlayer2 == 40 && !tieBreak) {
                textViewDeuce.setText(getString(R.string.deuce));
            }
            /**
             * manage tiebreak service
             */
            if (tieBreak) {
                numberOfServeInTieBreak++;
                if (pointsPlayer1 + pointsPlayer2 == 1) {
                    serveOfPlayerInTieBreak = !serveOfPlayerInTieBreak;
                    numberOfServeInTieBreak = 0;
                }
                if (numberOfServeInTieBreak > 1) {
                    serveOfPlayerInTieBreak = !serveOfPlayerInTieBreak;
                    numberOfServeInTieBreak = 0;
                }
                serveChange(serveOfPlayerInTieBreak);
            }
            if (serveOfPlayer && pointsPlayer2 == 40 && !tieBreak && pointsPlayer1 < 40) {
                textViewDeuce.setText(getString(R.string.break_point_player2));
            }
            /**
             * check if player has Win the tiebreak
             */
            if (pointsPlayer2 >= 6 && pointsPlayer2 > pointsPlayer1 + 1 && tieBreak) {
                gamesPlayer2++;
                displayGamesForPlayer2(gamesPlayer2);
                tieBreak = false;
            }
            return;
        }
        if (points == pointsPlayer1) {
            scoreViewPlayer1.setText(getString(R.string.points_40));
            scoreViewPlayer2.setText(getString(R.string.points_40));
            textViewDeuce.setText(getString(R.string.deuce) + " (" + (pointsPlayer1 - 39) + ")");
            return;
        }
        if (points == pointsPlayer1 + 1) {
            scoreViewPlayer1.setText("");
            scoreViewPlayer2.setText(getString(R.string.advance));
            if (serveOfPlayer) {
                textViewDeuce.setText(getString(R.string.break_point_player2));
            }
            return;
        }
        if (points == pointsPlayer1 + 2) {
            gamesPlayer2++;
            displayGamesForPlayer2(gamesPlayer2);
        }
    }


    /**
     * Displays the score of GAMES for Player 1. and reset points
     */
    public void displayGamesForPlayer1(int points) {
        TextView gamesView = (TextView) findViewById(R.id.player_1_games);
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        gamesView.setText(String.valueOf(points));
        pointsPlayer1 = 0;
        pointsPlayer2 = 0;
        scoreViewPlayer1.setText("0");
        scoreViewPlayer2.setText("0");
        textViewDeuce.setText(getString(R.string.game_for_player1));
        /**
         * change serve only if not playing tiebreak
         */
        serveOfPlayer = !serveOfPlayer;
        serveChange(serveOfPlayer);
        /**
         * check if player has win the set
         */
        if (gamesPlayer1 >= 6 && gamesPlayer1 > gamesPlayer2 + 1 || gamesPlayer1 == 7) {
            setsPlayer1++;
            displaySetsForPlayer1(setsPlayer1);
            return;
        }
        /**
         * check if tiebreak needs to be played
         */
        if (gamesPlayer1 == 6 && gamesPlayer2 == 6 && tiebreakFinal) {
            tieBreak = true;
            textViewDeuce.setText(getString(R.string.tiebreak));
            /**
             * in tiebreak first serve has player who is next on serve in order and tiebreak is like one game,
             * so they change serve in tiebreak but continue with normal serving after.
             *
             */
            serveOfPlayerInTieBreak = serveOfPlayer;
            serveChange(serveOfPlayerInTieBreak);
        }
    }

    /**
     * Displays the score of GAMES for Player 2. and reset points
     */
    public void displayGamesForPlayer2(int points) {
        TextView gamesView = (TextView) findViewById(R.id.player_2_games);
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        gamesView.setText(String.valueOf(points));
        pointsPlayer1 = 0;
        pointsPlayer2 = 0;
        scoreViewPlayer1.setText("0");
        scoreViewPlayer2.setText("0");
        textViewDeuce.setText(getString(R.string.game_for_player2));
        /**
         * change serve only if not playing tiebreak
         */
        serveOfPlayer = !serveOfPlayer;
        serveChange(serveOfPlayer);
        /**
         * check if player has win the set
         */
        if (gamesPlayer2 >= 6 && gamesPlayer2 > gamesPlayer1 + 1 || gamesPlayer2 == 7) {
            setsPlayer2++;
            displaySetsForPlayer2(setsPlayer2);
            return;
        }
        /**
         * check if tiebreak needs to be played
         */
        if (gamesPlayer1 == 6 && gamesPlayer2 == 6 && tiebreakFinal) {
            tieBreak = true;
            textViewDeuce.setText(getString(R.string.tiebreak));
            /**
             * in tiebreak first serve has player who is next on serve in order and tiebreak is like one game,
             * so they change serve in tiebreak but continue with normal serving after.
             *
             */
            serveOfPlayerInTieBreak = serveOfPlayer;
            serveChange(serveOfPlayerInTieBreak);
        }
    }

    /**
     * Displays the score of SETS for Player 1. and reset points for games
     */
    public void displaySetsForPlayer1(int points) {
        TextView setViewPlayer1 = (TextView) findViewById(R.id.player_1_sets);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        /**
         * Save GAMES before resetting to 0
         */
        if (setsPlayer1 + setsPlayer2 == 1) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_1);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_1);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 2) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_2);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_2);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 3) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_3);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_3);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 4) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_4);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_4);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 5) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_5);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_5);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        /**
         * set Games and POINTS to 0 and manage text
         */
        gamesPlayer1 = 0;
        gamesPlayer2 = 0;
        TextView gamesViewPlayer1 = (TextView) findViewById(R.id.player_1_games);
        TextView gamesViewPlayer2 = (TextView) findViewById(R.id.player_2_games);
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        gamesViewPlayer1.setText("0");
        gamesViewPlayer2.setText("0");
        scoreViewPlayer1.setText("0");
        scoreViewPlayer2.setText("0");
        setViewPlayer1.setText(String.valueOf(points));
        textViewDeuce.setText(getString(R.string.set_for_player1));
        /**
         * check if player has WIN the match
         * hide buttons
         */
        if (numberOfSetsForWin == setsPlayer1) {
            textViewDeuce.setText(getString(R.string.game_set_match_player1));
            LinearLayout linearLayoutAllButtonsHolder = (LinearLayout) findViewById(R.id.buttons_layout_holder);
            linearLayoutAllButtonsHolder.setVisibility(View.GONE);
            printStatistic();
            matchWon = true;
            TextView textViewWinner = (TextView) findViewById(R.id.serve_color_Player1);
            textViewWinner.setText(getString(R.string.winner_player));

        }
    }

    /**
     * Displays the score of SETS for Player 2. and reset points for games
     */
    public void displaySetsForPlayer2(int points) {
        TextView setViewPlayer2 = (TextView) findViewById(R.id.player_2_sets);
        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
        /**
         * Save GAMES before resetting to 0
         */
        if (setsPlayer1 + setsPlayer2 == 1) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_1);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_1);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 2) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_2);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_2);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 3) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_3);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_3);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 4) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_4);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_4);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        if (setsPlayer1 + setsPlayer2 == 5) {
            TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_5);
            TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_5);
            saveSetViewPlayer1.setText(String.valueOf(gamesPlayer1));
            saveSetViewPlayer2.setText(String.valueOf(gamesPlayer2));
        }
        /**
         * set Games and POINTS to 0 and manage text
         */
        gamesPlayer1 = 0;
        gamesPlayer2 = 0;
        TextView gamesViewPlayer1 = (TextView) findViewById(R.id.player_1_games);
        TextView gamesViewPlayer2 = (TextView) findViewById(R.id.player_2_games);
        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
        gamesViewPlayer1.setText("0");
        gamesViewPlayer2.setText("0");
        scoreViewPlayer1.setText("0");
        scoreViewPlayer2.setText("0");
        setViewPlayer2.setText(String.valueOf(points));
        textViewDeuce.setText(getString(R.string.set_for_player2));
        /**
         * Check if player has WIN the match
         * hides buttons
         */
        if (numberOfSetsForWin == setsPlayer2) {
            textViewDeuce.setText(getString(R.string.game_set_match_player2));
            LinearLayout linearLayoutAllButtonsHolder = (LinearLayout) findViewById(R.id.buttons_layout_holder);
            linearLayoutAllButtonsHolder.setVisibility(View.GONE);
            printStatistic();
            matchWon = true;
            TextView textViewWinner = (TextView) findViewById(R.id.serve_color_Player2);
            textViewWinner.setText(getString(R.string.winner_player));
        }
    }

    /**
     * ------------------------Manage points for  Player 1.---------------------------------------
     */
    public void managePointsPlayer1() {
        if (!tieBreak) {
            if (pointsPlayer1 == 0 | pointsPlayer1 == 15) {
                pointsPlayer1 = pointsPlayer1 + 15;
                displayPointsForPlayer1(pointsPlayer1);
                return;
            }
            if (pointsPlayer1 == 30) {
                pointsPlayer1 = pointsPlayer1 + 10;
                displayPointsForPlayer1(pointsPlayer1);
                return;
            }
            if (pointsPlayer1 >= 40) {
                pointsPlayer1++;
                if (pointsPlayer2 < 40) {
                    gamesPlayer1++;
                    displayGamesForPlayer1(gamesPlayer1);
                    return;
                }
                if (pointsPlayer1 >= pointsPlayer2) {
                    displayPointsForPlayer1(pointsPlayer1);
                    return;
                }
                if (pointsPlayer1 == pointsPlayer2 + 2) {
                    gamesPlayer1++;
                    displayGamesForPlayer1(gamesPlayer1);
                }
            }
        } else {
            pointsPlayer1++;
            displayPointsForPlayer1(pointsPlayer1);
        }


    }

    /**
     * winner points for player 1
     */
    public void addPointForPlayer1(View v) {
        winnerPlayer1++;
        managePointsPlayer1();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
        }
    }

    /**
     * ace points for player 1
     */
    public void addPointForPlayer1Ace(View v) {
        acePlayer1++;
        managePointsPlayer1();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
        }
    }

    /**
     * forced error button player 1
     */
    public void addPointForPlayer1ForError(View v) {
        forcedErrorPlayer1++;
        managePointsPlayer2();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
        }
    }

    /**
     * unforced error button player 1
     */
    public void addPointForPlayer1UnfError(View v) {
        unforcedErrorPlayer1++;
        managePointsPlayer2();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
        }
    }

    /**
     * fault and double fault
     */
    public void addPointForPlayer1Fault(View v) {
        if (firstFault) {
            faultPlayer1++;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.double_fault));
            firstFault = false;
        } else {
            doubleFaultPlayer1++;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
            firstFault = true;
            managePointsPlayer2();
        }
    }

    /**
     * -------------------------------Manage points for  Player 2.-------------------------
     */
    public void managePointsPlayer2() {
        if (!tieBreak) {
            if (pointsPlayer2 == 0 | pointsPlayer2 == 15) {
                pointsPlayer2 = pointsPlayer2 + 15;
                displayPointsForPlayer2(pointsPlayer2);
                return;
            }
            if (pointsPlayer2 == 30) {
                pointsPlayer2 = pointsPlayer2 + 10;
                displayPointsForPlayer2(pointsPlayer2);
                return;
            }
            if (pointsPlayer2 >= 40) {
                pointsPlayer2++;
                if (pointsPlayer1 < 40) {
                    gamesPlayer2++;
                    displayGamesForPlayer2(gamesPlayer2);
                    return;
                }
                if (pointsPlayer2 >= pointsPlayer1) {
                    displayPointsForPlayer2(pointsPlayer2);
                    return;
                }
                if (pointsPlayer2 == pointsPlayer1 + 2) {
                    gamesPlayer2++;
                    displayGamesForPlayer2(gamesPlayer2);
                }
            }
        } else {
            pointsPlayer2++;
            displayPointsForPlayer2(pointsPlayer2);
        }
    }

    /**
     * winner points for player 2
     */
    public void addPointForPlayer2(View v) {
        winnerPlayer2++;
        managePointsPlayer2();
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer1 = (Button) findViewById(R.id.button_player1_fault);
            buttonFaultPlayer1.setText(getString(R.string.fault));
        }
    }

    /**
     * ace points for player 2
     */
    public void addPointForPlayer2Ace(View v) {
        acePlayer1++;
        managePointsPlayer2();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer2 = (Button) findViewById(R.id.button_player2_fault);
            buttonFaultPlayer2.setText(getString(R.string.fault));
        }
    }

    /**
     * forced error for player 2
     */
    public void addPointForPlayer2ForError(View v) {
        forcedErrorPlayer2++;
        managePointsPlayer1();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer2 = (Button) findViewById(R.id.button_player2_fault);
            buttonFaultPlayer2.setText(getString(R.string.fault));
        }
    }

    public void addPointForPlayer2UnfError(View v) {
        unforcedErrorPlayer2++;
        managePointsPlayer1();
        /**
         * set first fault to 0
         */
        if (!firstFault) {
            firstFault = true;
            Button buttonFaultPlayer2 = (Button) findViewById(R.id.button_player2_fault);
            buttonFaultPlayer2.setText(getString(R.string.fault));
        }
    }

    /**
     * fault and double fault for player 2
     */
    public void addPointForPlayer2Fault(View v) {
        if (firstFault) {
            faultPlayer2++;
            Button buttonFaultPlayer2 = (Button) findViewById(R.id.button_player2_fault);
            buttonFaultPlayer2.setText(getString(R.string.double_fault));
            firstFault = false;
        } else {
            doubleFaultPlayer2++;
            Button buttonFaultPlayer2 = (Button) findViewById(R.id.button_player2_fault);
            buttonFaultPlayer2.setText(getString(R.string.fault));
            firstFault = true;
            managePointsPlayer1();
        }
    }


    /**
     * change who serve first
     */

    public void changeServeFirstPlayer(View view) {
        if (winnerPlayer1 == 0 && winnerPlayer2 == 0 && acePlayer1 == 0 &&
                acePlayer2 == 0 && faultPlayer1 == 0 && faultPlayer2 == 0 &&
                forcedErrorPlayer1 == 0 && forcedErrorPlayer2 == 0 && unforcedErrorPlayer1 == 0 && unforcedErrorPlayer2 == 0) {
            serveOfPlayer = !serveOfPlayer;
            serveChange(serveOfPlayer);
        } else {
            Toast.makeText(this, getString(R.string.toast_message_serve), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * change service method
     */

    public void serveChange(boolean serve) {
        if (serve) {
            TextView textViewServePlayer1 = (TextView) findViewById(R.id.serve_color_Player1);
            TextView textViewServePlayer2 = (TextView) findViewById(R.id.serve_color_Player2);
            textViewServePlayer1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            textViewServePlayer2.setBackgroundColor(Color.TRANSPARENT);
            textViewServePlayer1.setText(getString(R.string.serve));
            textViewServePlayer2.setText("");
            Button buttonPlayer2Ace = (Button) findViewById(R.id.button_player2_ace);
            Button buttonPlayer2Fault = (Button) findViewById(R.id.button_player2_fault);
            buttonPlayer2Ace.setVisibility(View.INVISIBLE);
            buttonPlayer2Fault.setVisibility(View.INVISIBLE);
            Button buttonPlayer1Ace = (Button) findViewById(R.id.button_player1_ace);
            Button buttonPlayer1Fault = (Button) findViewById(R.id.button_player1_fault);
            buttonPlayer1Ace.setVisibility(View.VISIBLE);
            buttonPlayer1Fault.setVisibility(View.VISIBLE);
        } else {
            TextView textViewServePlayer1 = (TextView) findViewById(R.id.serve_color_Player1);
            TextView textViewServePlayer2 = (TextView) findViewById(R.id.serve_color_Player2);
            textViewServePlayer1.setBackgroundColor(Color.TRANSPARENT);
            textViewServePlayer2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            textViewServePlayer1.setText("");
            textViewServePlayer2.setText(getString(R.string.serve));
            Button buttonPlayer2Ace = (Button) findViewById(R.id.button_player2_ace);
            Button buttonPlayer2Fault = (Button) findViewById(R.id.button_player2_fault);
            buttonPlayer2Ace.setVisibility(View.VISIBLE);
            buttonPlayer2Fault.setVisibility(View.VISIBLE);
            Button buttonPlayer1Ace = (Button) findViewById(R.id.button_player1_ace);
            Button buttonPlayer1Fault = (Button) findViewById(R.id.button_player1_fault);
            buttonPlayer1Ace.setVisibility(View.INVISIBLE);
            buttonPlayer1Fault.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * RESET ALL To default
     */
    public void resetAll(View v) {

        new AlertDialog.Builder(this)
                .setTitle("Reset Match")
                .setMessage("Do you really want to reset?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MainActivity.this, "Match reset", Toast.LENGTH_SHORT).show();
                        /**
                         * set all variables to default state
                         */
                        pointsPlayer1 = 0;
                        pointsPlayer2 = 0;
                        gamesPlayer1 = 0;
                        gamesPlayer2 = 0;
                        setsPlayer1 = 0;
                        setsPlayer2 = 0;
                        numberOfSetsForWin = 2;
                        serveOfPlayer = true;
                        serveOfPlayerInTieBreak = true;
                        tieBreak = false;
                        firstFault = true;
                        showStatistic = true;
                        matchWon = false;

                        winnerPlayer1 = 0;
                        acePlayer1 = 0;
                        faultPlayer1 = 0;
                        doubleFaultPlayer1 = 0;
                        forcedErrorPlayer1 = 0;
                        unforcedErrorPlayer1 = 0;

                        winnerPlayer2 = 0;
                        acePlayer2 = 0;
                        faultPlayer2 = 0;
                        doubleFaultPlayer2 = 0;
                        forcedErrorPlayer2 = 0;
                        unforcedErrorPlayer2 = 0;
                        /**
                         * reset POINTS
                         */
                        TextView scoreViewPlayer1 = (TextView) findViewById(R.id.player_1_points);
                        TextView scoreViewPlayer2 = (TextView) findViewById(R.id.player_2_points);
                        TextView textViewDeuce = (TextView) findViewById(R.id.deuce);
                        scoreViewPlayer1.setText("0");
                        scoreViewPlayer2.setText("0");
                        textViewDeuce.setText("");

                        /**
                         * reset GAMES
                         */
                        TextView gamesView1 = (TextView) findViewById(R.id.player_1_games);
                        TextView gamesView2 = (TextView) findViewById(R.id.player_2_games);
                        gamesView1.setText("0");
                        gamesView2.setText("0");

                        /**
                         * reset SETS
                         */
                        TextView setViewPlayer1 = (TextView) findViewById(R.id.player_1_sets);
                        TextView setViewPlayer2 = (TextView) findViewById(R.id.player_2_sets);
                        setViewPlayer1.setText("0");
                        setViewPlayer2.setText("0");


                        /**
                         * reset saved SETS
                         */
                        TextView saveSetViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_1);
                        TextView saveSetViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_1);
                        saveSetViewPlayer1.setText("");
                        saveSetViewPlayer2.setText("");

                        TextView saveSet2ViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_2);
                        TextView saveSet2ViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_2);
                        saveSet2ViewPlayer1.setText("");
                        saveSet2ViewPlayer2.setText("");

                        TextView saveSet3ViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_3);
                        TextView saveSet3ViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_3);
                        saveSet3ViewPlayer1.setText("");
                        saveSet3ViewPlayer2.setText("");

                        TextView saveSet4ViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_4);
                        TextView saveSet4ViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_4);
                        saveSet4ViewPlayer1.setText("");
                        saveSet4ViewPlayer2.setText("");

                        TextView saveSet5ViewPlayer1 = (TextView) findViewById(R.id.save_sets_player1_set_5);
                        TextView saveSet5ViewPlayer2 = (TextView) findViewById(R.id.save_sets_player2_set_5);
                        saveSet5ViewPlayer1.setText("");
                        saveSet5ViewPlayer2.setText("");

                        /**
                         * show buttons
                         */
                        LinearLayout linearLayoutAllButtonsHolder = (LinearLayout) findViewById(R.id.buttons_layout_holder);
                        linearLayoutAllButtonsHolder.setVisibility(View.VISIBLE);
                        LinearLayout linearLayoutStatistic = (LinearLayout) findViewById(R.id.layout_statistic);
                        linearLayoutStatistic.setVisibility(View.GONE);


                        /**
                         * return serve to player 1 first and remove winner text
                         */

                        TextView textViewWinner1 = (TextView) findViewById(R.id.serve_color_Player1);
                        textViewWinner1.setText("");
                        TextView textViewWinner2 = (TextView) findViewById(R.id.serve_color_Player2);
                        textViewWinner2.setText("");
                        serveChange(serveOfPlayer);

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }

    /**
     * manage statistic and show it in textViews
     */
    public void printStatistic() {
        /**
         * show statistic and hide buttons
         */
        LinearLayout linearLayoutStatistic = (LinearLayout) findViewById(R.id.layout_statistic);
        linearLayoutStatistic.setVisibility(View.VISIBLE);
        LinearLayout linearLayoutAllButtonsHolder = (LinearLayout) findViewById(R.id.buttons_layout_holder);
        linearLayoutAllButtonsHolder.setVisibility(View.GONE);
        /**
         * Player 1 statistic
         */
        TextView textViewPlayer1Winners = (TextView) findViewById(R.id.textview_player1_winners);
        TextView textViewPlayer1Aces = (TextView) findViewById(R.id.textview_player1_aces);
        TextView textViewPlayer1Fault = (TextView) findViewById(R.id.textview_player1_faults);
        TextView textViewPlayer1DoubleFault = (TextView) findViewById(R.id.textview_player1_double_faults);
        TextView textViewPlayer1ForcedErrors = (TextView) findViewById(R.id.textview_player1_forced_errors);
        TextView textViewPlayer1UnforcedErrors = (TextView) findViewById(R.id.textview_player1_unforced_errors);
        textViewPlayer1Winners.setText(getString(R.string.winners) + winnerPlayer1);
        textViewPlayer1Aces.setText(getString(R.string.aces) + acePlayer1);
        textViewPlayer1Fault.setText(getString(R.string.faults) + faultPlayer1);
        textViewPlayer1DoubleFault.setText(getString(R.string.double_faults) + doubleFaultPlayer1);
        textViewPlayer1ForcedErrors.setText(getString(R.string.forced_errors) + forcedErrorPlayer1);
        textViewPlayer1UnforcedErrors.setText(getString(R.string.unforced_errors) + unforcedErrorPlayer1);
        /**
         * player 2 statistic
         */
        TextView textViewPlayer2Winners = (TextView) findViewById(R.id.textview_player2_winners);
        TextView textViewPlayer2Aces = (TextView) findViewById(R.id.textview_player2_aces);
        TextView textViewPlayer2Fault = (TextView) findViewById(R.id.textview_player2_faults);
        TextView textViewPlayer2DoubleFault = (TextView) findViewById(R.id.textview_player2_double_faults);
        TextView textViewPlayer2ForcedErrors = (TextView) findViewById(R.id.textview_player2_forced_errors);
        TextView textViewPlayer2UnforcedErrors = (TextView) findViewById(R.id.textview_player2_unforced_errors);
        textViewPlayer2Winners.setText(getString(R.string.winners) + winnerPlayer2);
        textViewPlayer2Aces.setText(getString(R.string.aces) + acePlayer2);
        textViewPlayer2Fault.setText(getString(R.string.faults) + faultPlayer2);
        textViewPlayer2DoubleFault.setText(getString(R.string.double_faults) + doubleFaultPlayer2);
        textViewPlayer2ForcedErrors.setText(getString(R.string.forced_errors) + forcedErrorPlayer2);
        textViewPlayer2UnforcedErrors.setText(getString(R.string.unforced_errors) + unforcedErrorPlayer2);
    }

    /**
     * show and hide statistic for button
     */
    public void showHideStatistic(View v) {
        if (!matchWon) {
            if (showStatistic) {
                showStatistic = false;
                printStatistic();
            } else {
                LinearLayout linearLayoutStatistic = (LinearLayout) findViewById(R.id.layout_statistic);
                linearLayoutStatistic.setVisibility(View.GONE);
                LinearLayout linearLayoutAllButtonsHolder = (LinearLayout) findViewById(R.id.buttons_layout_holder);
                linearLayoutAllButtonsHolder.setVisibility(View.VISIBLE);
                showStatistic = true;
            }
        }
    }

    /**
     * changing players names
     * @param v
     */
    public void changePlayersNames(View v) {
        RelativeLayout playersBoxLayout = (RelativeLayout) findViewById(R.id.change_players_box);
        playersBoxLayout.setVisibility(View.VISIBLE);
    }

    public void changePlayersNamesSubmit(View v) {
        RelativeLayout playersBoxLayout = (RelativeLayout) findViewById(R.id.change_players_box);
        playersBoxLayout.setVisibility(View.GONE);
        TextView player1textView = (TextView) findViewById(R.id.player1_name);
        TextView player2textView = (TextView) findViewById(R.id.player2_name);
        TextView player1textViewServe = (TextView) findViewById(R.id.player1_name_serve);
        TextView player2textViewServe = (TextView) findViewById(R.id.player2_name_serve);
        EditText player1EditText = (EditText) findViewById(R.id.player1_change_name);
        EditText player2EditText = (EditText) findViewById(R.id.player2_change_name);
        player1textView.setText(player1EditText.getText());
        player2textView.setText(player2EditText.getText());
        player1textViewServe.setText(player1EditText.getText());
        player2textViewServe.setText(player2EditText.getText());
    }

}
