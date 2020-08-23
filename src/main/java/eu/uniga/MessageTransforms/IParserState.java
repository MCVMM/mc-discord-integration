package eu.uniga.MessageTransforms;

public interface IParserState<Self extends IParserState<Self>>
{
	Self NewBlockQuoteState(boolean isInQuote);
	boolean IsInQuote();
}