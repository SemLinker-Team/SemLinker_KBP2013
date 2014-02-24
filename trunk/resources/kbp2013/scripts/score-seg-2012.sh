EXPE=/HOME_DIR/EXPERIMENT_DIR
TESTREP=test2012
echo ----------------------------------------------------------
echo ----- Score Global 
echo ----------------------------------------------------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/
echo
echo
echo ----- Score sur PERS ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --PER
echo ----- Score sur ORG ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --ORG
echo ----- Score sur GPE ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --GPE
echo ----- Score sur NIL ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --NIL
echo ----- Score sur KB ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --NONIL
echo
echo ----- Score sur News ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --NW
echo ----- Score sur Web ---------------
python el_scorer_seg.py $EXPE/tac_2012_kbp_english_evaluation_entity_linking_query_types.tab $EXPE/$TESTREP/ --WB
