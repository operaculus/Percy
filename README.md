Percy: A LDA implementation with Scala.


PercyExp里包含了用来转换训练数据和模型的脚本，可以使用LDA作者Blei提供的数据集做测试(http://www.cs.princeton.edu/~blei/lda-c/ap.tgz)。

现有LDA的工具中，LDA-C是单线程的，难以做到大规模数据处理，LDA-Giibs同样，而且Gibbs算法很难并行化。
尽管PLDA使用一种不太雅观的方法做到了并行，但是由于需要在内存保存多个模型，过于耗费内存。
其他能做到并行化的如Mr.LDA，需要依赖笨重难用的Hadoop。

在单机并行化越来越强大和内存廉价的今天，需要一个单机LDA工具，因此我开发了它，使用了Scala语言，欢迎使用。
任何问题请联系我，tengfeiDOTbao#gmailDOTcom

