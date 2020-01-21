@#4928-BDD
Feature: Check that ServiceTemplate CF are visible during createOfferFromBOM

  Background: #4928-BDD.json is executed

  @admin @superadmin
  Scenario Outline: Check that ServiceTemplate CF are visible during createOfferFromBOM
    Given The entity has the following information "<jsonFile>" as "<dto>"
    When I call the "<api>"
    Then I got the Service template  with a custom fields 
    And  "<Val_aString_attendue>" should be equal to file "<Val_aString_actuelle>"
    And  "<Val_aStringFiltered_attendue>" should be equal to file "<Val_aStringFiltered_actuelle>"
    

    Examples: 
      | jsonFile                    | dto                              | api                           | statusCode | status  | Val_aString_attendue       | Val_aString_actuelle       | Val_aStringFiltered_attendue      |Val_aStringFiltered_actuelle      |
      | #4928-BDD/SuccessTest1.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_1.txt | Val_aString_actuelle_1.txt | Val_aStringFiltered_attendue_1.txt|Val_aStringFiltered_actuelle_1.txt|
      | #4928-BDD/SuccessTest2.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_2.txt | Val_aString_actuelle_2.txt | Val_aStringFiltered_attendue_2.txt|Val_aStringFiltered_actuelle_2.txt|
      | #4928-BDD/SuccessTest3.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_3.txt | Val_aString_actuelle_3.txt | Val_aStringFiltered_attendue_3.txt|Val_aStringFiltered_actuelle_3.txt|
      | #4928-BDD/SuccessTest5.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_4.txt | Val_aString_actuelle_4.txt | Val_aStringFiltered_attendue_4.txt|Val_aStringFiltered_actuelle_4.txt|
      | #4928-BDD/SuccessTest5.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_5.txt | Val_aString_actuelle_5.txt | Val_aStringFiltered_attendue_5.txt|Val_aStringFiltered_actuelle_5.txt|
      | #4928-BDD/SuccessTest6.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_6.txt | Val_aString_actuelle_6.txt | Val_aStringFiltered_attendue_6.txt|Val_aStringFiltered_actuelle_6.txt|
      | #4928-BDD/SuccessTest7.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_7.txt | Val_aString_actuelle_7.txt | Val_aStringFiltered_attendue_7.txt|Val_aStringFiltered_actuelle_7.txt|
      | #4928-BDD/SuccessTest8.json |GetListServiceTemplateResponseDto | /catalog/serviceTemplate/list |   200      | SUCCESS | Val_aString_attendue_8.txt | Val_aString_actuelle_8.txt | Val_aStringFiltered_attendue_8.txt|Val_aStringFiltered_actuelle_8.txt|


