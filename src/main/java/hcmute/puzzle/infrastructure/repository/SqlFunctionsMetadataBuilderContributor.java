//package hcmute.puzzle.infrastructure.repository;
//
//import org.hibernate.boot.MetadataBuilder;
//import org.hibernate.boot.ResourceStreamLocator;
//import org.hibernate.boot.model.FunctionContributions;
//import org.hibernate.boot.model.TypeContributions;
//import org.hibernate.boot.model.TypeContributor;
//import org.hibernate.boot.model.FunctionContributor;
//import org.hibernate.boot.spi.AdditionalMappingContributions;
//import org.hibernate.boot.spi.AdditionalMappingContributor;
//import org.hibernate.boot.spi.InFlightMetadataCollector;
//import org.hibernate.boot.spi.MetadataBuildingContext;
//import org.hibernate.service.ServiceRegistry;
//
//
//public class SqlFunctionsMetadataBuilderContributor implements AdditionalMappingContributor {
//	@Override
//	public void contribute(MetadataBuilder metadataBuilder) {
//		metadataBuilder.applySqlFunction("fts",
//										 new SQLFunctionTemplate(BooleanType.INSTANCE,
//																 "to_tsvector(description) @@ plainto_tsquery(?1)"));
//	}
//
//	@Override
//	public void contribute(AdditionalMappingContributions additionalMappingContributions,
//			InFlightMetadataCollector inFlightMetadataCollector, ResourceStreamLocator resourceStreamLocator,
//			MetadataBuildingContext metadataBuildingContext) {
//		metadataBuildingContext.getBuildingOptions().getServiceRegistry().
//
//	}
//
//}
