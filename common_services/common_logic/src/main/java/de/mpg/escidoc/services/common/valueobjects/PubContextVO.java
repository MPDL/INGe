package de.mpg.escidoc.services.common.valueobjects;

public class PubContextVO extends ContextVO {
    
//    /**
//     * The possible genre values for items of the collection.
//     */
//    private java.util.List<MdsPublicationVO.Genre> allowedGenres = new java.util.ArrayList<MdsPublicationVO.Genre>();
//
    /**
     * Default constructor.
     */
    public PubContextVO()
    {
        
    }
    
    /**
     * Clone constructor.
     * 
     * @param contextVO The context to be copied.
     */
    public PubContextVO(PubContextVO contextVO)
    {
        super(contextVO);
    }
//    
//    public PubContextVO(PubContextVO context)
//    {
//        super(context);
//        this.allowedGenres = context.allowedGenres;
//    }
//
//    /**
//     * Delivers the list of allowed genres in the collection, i. e. the allowed genres for items of the collection.
//     */
//    public java.util.List<MdsPublicationVO.Genre> getAllowedGenres()
//    {
//        return allowedGenres;
//    }

}
