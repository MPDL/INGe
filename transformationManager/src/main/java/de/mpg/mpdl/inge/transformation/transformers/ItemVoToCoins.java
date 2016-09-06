package de.mpg.mpdl.inge.transformation.transformers;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerResult;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerSource;
import de.mpg.mpdl.inge.transformation.sources.VoSource;
import de.mpg.mpdl.inge.transformation.transformers.helpers.coins.CoinsTransformation;

@TransformerModule(sourceFormat = FORMAT.ESCIDOC_ITEM_VO, targetFormat = FORMAT.COINS_STRING)
public class ItemVoToCoins extends SingleTransformer implements ChainableTransformer {

	@Override
	public void transform(TransformerSource source, TransformerResult result) throws TransformationException {
		VoSource s = (VoSource)source;
		
		CoinsTransformation coinsTransformation = new CoinsTransformation();
		String stringResult = coinsTransformation.getCOinS((PubItemVO)s.getSource());
		
		writeStringToStreamResult(stringResult, (TransformerStreamResult)result);

	}

	@Override
	public TransformerResult createNewInBetweenResult() {
		// TODO Auto-generated method stub
		return null;
	}




}
