EXPE=/HOME_DIR/EXPERIMENT_DIR
TESTREP=test2013
echo ----------------------------------------------------------
echo ----- Score Global 
echo ----------------------------------------------------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/
echo
echo

echo ----- Score sur KB ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --NONIL
echo ----- Score sur NIL ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --NIL
echo

echo
echo ----- Score sur News ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --NW
echo ----- Score sur Web ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --WB
echo ----- Score sur Forum ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --DF
echo

echo ----- Score sur PERS ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --PER
echo ----- Score sur ORG ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --ORG
echo ----- Score sur GPE ---------------
python el_scorer_seg.py $EXPE/tac_2013_kbp_english_entity_linking_evaluation_KB_links.tab $EXPE/$TESTREP/ --GPE



