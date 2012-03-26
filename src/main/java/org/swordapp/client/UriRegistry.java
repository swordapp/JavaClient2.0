package org.swordapp.client;

public class UriRegistry
{
    // Namespaces
    public static String SWORD_TERMS_NAMESPACE = "http://purl.org/net/sword/terms/";
    public static String APP_NAMESPACE = "http://www.w3.org/2007/app";
    public static String DC_NAMESPACE = "http://purl.org/dc/terms/";
    public static String ERROR_NAMESPACE = "http://purl.org/net/sword/error/";
    public static String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";

    // QNames for Extension Elements
    public static SQName SWORD_VERSION = new SQName(SWORD_TERMS_NAMESPACE, "version");
    public static SQName SWORD_MAX_UPLOAD_SIZE = new SQName(SWORD_TERMS_NAMESPACE, "maxUploadSize");
    public static SQName SWORD_COLLECTION_POLICY = new SQName(SWORD_TERMS_NAMESPACE, "collectionPolicy");
    public static SQName SWORD_MEDIATION = new SQName(SWORD_TERMS_NAMESPACE, "mediation");
    public static SQName SWORD_TREATMENT = new SQName(SWORD_TERMS_NAMESPACE, "treatment");
    public static SQName SWORD_ACCEPT_PACKAGING = new SQName(SWORD_TERMS_NAMESPACE, "acceptPackaging");
    public static SQName SWORD_SERVICE = new SQName(SWORD_TERMS_NAMESPACE, "service");
	public static SQName SWORD_PACKAGING = new SQName(SWORD_TERMS_NAMESPACE, "packaging");
    public static SQName SWORD_VERBOSE_DESCRIPTION = new SQName(SWORD_TERMS_NAMESPACE, "verboseDescription");
    public static SQName DC_ABSTRACT = new SQName(DC_NAMESPACE, "abstract");

    public static SQName SWORD_DEPOSITED_BY = new SQName(SWORD_TERMS_NAMESPACE, "depositedBy");
    public static SQName SWORD_DEPOSITED_ON = new SQName(SWORD_TERMS_NAMESPACE, "depositedOn");
    public static SQName SWORD_DEPOSITED_ON_BEHALF_OF = new SQName(SWORD_TERMS_NAMESPACE, "depositedOnBehalfOf");
    public static SQName SWORD_STATE = new SQName(SWORD_TERMS_NAMESPACE, "state");
    public static SQName SWORD_STATE_DESCRIPTION = new SQName(SWORD_TERMS_NAMESPACE, "stateDescription");

	// rel values
	public static String REL_SERVICE_DOCUMENT = "http://purl.org/net/sword/discovery/service-document";
	public static String REL_DEPOSIT = SWORD_TERMS_NAMESPACE + "deposit";
	public static String REL_EDIT = SWORD_TERMS_NAMESPACE + "edit";
	public static String REL_STATEMENT = SWORD_TERMS_NAMESPACE + "statement";
    public static String REL_SWORD_EDIT = SWORD_TERMS_NAMESPACE + "add";
    public static String REL_ORIGINAL_DEPOSIT = SWORD_TERMS_NAMESPACE + "originalDeposit";
    public static String REL_DERIVED_RESOURCE = SWORD_TERMS_NAMESPACE + "derivedResource";

    // Package Formats
    public static String PACKAGE_SIMPLE_ZIP = "http://purl.org/net/sword/package/SimpleZip";
    public static String PACKAGE_BINARY = "http://purl.org/net/sword/package/Binary";

    // errors
    public static String ERROR_CONTENT = ERROR_NAMESPACE + "ErrorContent";
    public static String ERROR_CHECKSUM_MISMATCH = ERROR_NAMESPACE + "ErrorChecksumMismatch";
    public static String ERROR_BAD_REQUEST = ERROR_NAMESPACE + "ErrorBadRequest";
    public static String ERROR_TARGET_OWNER_UNKNOWN = ERROR_NAMESPACE + "TargetOwnerUnknown";
    public static String ERROR_MEDIATION_NOT_ALLOWED = ERROR_NAMESPACE + "MediationNotAllowed";
    public static String ERROR_METHOD_NOT_ALLOWED = ERROR_NAMESPACE + "MethodNotAllowed";
    public static String ERROR_MAX_UPLOAD_SIZE_EXCEEDED = ERROR_NAMESPACE + "MaxUploadSizeExceeded";
}
