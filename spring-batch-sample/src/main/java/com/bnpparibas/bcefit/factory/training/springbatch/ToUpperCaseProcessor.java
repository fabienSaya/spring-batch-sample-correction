package com.bnpparibas.bcefit.factory.training.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


/*
Une classe qui va traiter et/ou enrichir et/ou décorer l'object UserCSV lu depuis le fichier
en classe UserOut qu'on va retourner
 */

public class ToUpperCaseProcessor implements ItemProcessor<UserCSV, UserOut> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    // Méthode qui se charge de faire le traitement
    @Override
    public UserOut process(UserCSV userCSV) throws Exception {

        log.info("Process {}", userCSV);

        final UserOut out = new UserOut();
        out.setUid(userCSV.getUid());
        out.setNom(userCSV.getNom().toUpperCase());
        out.setPrenom(userCSV.getPrenom().toUpperCase());

        return out;
    }
}
