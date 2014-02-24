# Correspondance table between Wikimeta 
# and NIST KB for KBP 2013
# V0.1a Monday 15 July
#
# (c) E.Charton / École Polytechnique de Montréal / MItacs Elevate Research Fellow
# eric.charton@polymtl.ca
# !!! Please do not distribute / refer to us people interested !!!
#

Stats :

Currently, we manage 762491 entries from the official 
We have 24534 KB Unknown entries (mostly deleted pages since 2008)

Syntax :
All separated by ;

-Number of the KB
-KBKEY:ref_kb_number
-NEREF:ref_kb_named_NE_entity_tag
-NBASE:0/1
	flag to 1 if there is a direct correspondance between KB Wiki name and wiki link provided by Wikimeta
	flqag to 0 if there is and indirect or no correspondance
-full_text_for_the_wikikey returned by Wikimeta for related ref_kb_number
-NEENT:ref_NLGbAse_NE_entity_tag
-OKEY:original_KB_wiki_name
-NOOP no correspondance found

Samples:

27;KBKEY:E0000028;NEREF:UKN;NEBASE:0;Sabrina Online;NOOP

For this KB ref, there is no existing correspondance in wikipedia, NLGbAse and Wikimeta return

526490;KBKEY:E0531914;NEREF:UKN;NEBASE:1;The Arkham Collector: Volume I;NEENT:PROD.DOC;

For this KB ref, there is a direct link with Wikimeta URI output containing The_Arkham_Collector

526585;KBKEY:E0532010;NEREF:UKN;NEBASE:0;Se eu te pudesse abraçar;NEENT:PROD.ART;OKEY:Se Eu Te Pudesse Abraçar

For this KB ref, there is a correspondance with Wikimeta URI output containing Se_eu_te_pudesse_abraçar. The original KB Wiki was Se_Eu_Te_Pudesse_Abraçar
