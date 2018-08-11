# PDV: an integrative proteomics data viewer

[![Join the chat at https://gitter.im/PDV-public/Lobby](https://badges.gitter.im/PDV-public/Lobby.svg)](https://gitter.im/PDV-public/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) ![Downloads](https://img.shields.io/github/downloads/wenbostar/PDV/total.svg)

PDV is a JAVA tool for proteomics data visualization.

![alt text](https://github.com/wenbostar/PDV/blob/master/resources/PDV_main_panel.png)

## Usage

A user's manual and example dataset are available at [http://pdv.zhang-lab.org](http://pdv.zhang-lab.org).

## Installation

The PDV package can be downloaded at [https://github.com/wenbostar/PDV/releases](https://github.com/wenbostar/PDV/releases).

## Example

#### Database searching:

| Software        | Example files |
| ----------------|:---------------|
| [MS-GF+](https://github.com/MSGFPlus/msgfplus) (v2017.01.13)| [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/msgfplus/SF_200217_U2OS_TiO2_HCD_OT_rep1_mgf.mzid.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/msgfplus/SF_200217_U2OS_TiO2_HCD_OT_rep1_mzML.mzid.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/msgfplus/SF_200217_U2OS_TiO2_HCD_OT_rep1_mzXML.mzid.gz)|
| [X!Tandem](https://www.thegpm.org/tandem/) (v2017.2.1.2) | [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/xtandem/SF_200217_U2OS_TiO2_HCD_OT_rep1_mgf.mzid.gz) (convert X!Tandem XML result to mzid file using [MzidLib](https://github.com/PGB-LIV/mzidlib))|
| [MyriMatch](https://www.ncbi.nlm.nih.gov/pubmed/?term=17269722) (v2.2.10165) | [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/myrimatch/SF_200217_U2OS_TiO2_HCD_OT_rep1_myrimatch_mgf.mzid.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/myrimatch/SF_200217_U2OS_TiO2_HCD_OT_rep1_myrimatch_mzML.mzid.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/myrimatch/SF_200217_U2OS_TiO2_HCD_OT_rep1_myrimatch_mzXML.mzid.gz) |
| [Comet](http://comet-ms.sourceforge.net/) (v2018.01 rev. 2) | [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/comet/SF_200217_U2OS_TiO2_HCD_OT_rep1_mgf.pep.xml.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/comet/SF_200217_U2OS_TiO2_HCD_OT_rep1_mzML.pep.xml.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/comet/SF_200217_U2OS_TiO2_HCD_OT_rep1_mzXML.pep.xml.gz) |
| [Crux/Tide](http://crux.ms/) (v3.2)| [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mgf/tide-search.pep.xml.gz)<br> [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mgf/tide-search.mzid.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mzml/tide-search.pep.xml.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mzml/tide-search.mzid.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mzxml/tide-search.pep.xml.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/crux/crux-output_mzxml/tide-search.mzid.gz) |
| [MS Amanda](http://ms.imp.ac.at/index.php?action=ms-amanda) (v2.0.0.11219) | [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[csv](http://pdv.zhang-lab.org/data/download/test_data/msamanda/SF_200217_U2OS_TiO2_HCD_OT_rep1_MSAmanda_mgf.csv.gz)(MS Amanda format)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[csv](http://pdv.zhang-lab.org/data/download/test_data/msamanda/SF_200217_U2OS_TiO2_HCD_OT_rep1_MSAmanda_mzML.csv.gz)(MS Amanda format) |
| [MSFragger](https://www.nature.com/articles/nmeth.4256) (v20180316) | [mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/msfragger/SF_200217_U2OS_TiO2_HCD_OT_rep1_msfragger_mzML.pepXML.gz)<br>[mzXML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/msfragger/SF_200217_U2OS_TiO2_HCD_OT_rep1_msfragger_mzXML.pepXML.gz)|
| [MaxQuant](http://www.coxdocs.org/doku.php?id=maxquant:start) | [version 1.5.5.1](http://pdv.zhang-lab.org/data/download/test_data/maxquant/maxquant/1.5.5.1.tar.gz)<br>[version 1.5.8.3](http://pdv.zhang-lab.org/data/download/test_data/maxquant/maxquant/1.5.8.3.tar.gz)<br>[version 1.6.0.1](http://pdv.zhang-lab.org/data/download/test_data/maxquant/maxquant/1.6.0.1.tar.gz)<br>[version 1.6.2.3](http://pdv.zhang-lab.org/data/download/test_data/maxquant/maxquant/1.6.2.3.tar.gz) |
| [IPeak](https://www.ncbi.nlm.nih.gov/pubmed/25951428)| [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[mzid](http://pdv.zhang-lab.org/data/download/test_data/ipeak/SF_200217_U2OS_TiO2_HCD_OT_rep1_IPeak_mgf.mzid.gz) |
| [IdentiPy](https://bitbucket.org/levitsky/identipy) (v0.2)|[mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/identipy/SF_200217_U2OS_TiO2_HCD_OT_rep1_identipy_mgf.pep.xml.gz)<br>[mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[pepXML](http://pdv.zhang-lab.org/data/download/test_data/identipy/SF_200217_U2OS_TiO2_HCD_OT_rep1_identipy_mzML.pep.xml.gz)|
| [MetaMorpheus](https://github.com/smith-chem-wisc/MetaMorpheus) (v0.0.286) | [mzML](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz):[mzid]()|

#### Denovo sequencing:

| Software        | Example files |
| ----------------|:---------------|
| [Novor](https://www.ncbi.nlm.nih.gov/pubmed/26122521) | [mgf](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mgf.gz):[csv](http://pdv.zhang-lab.org/data/download/test_data/novor/SF_200217_U2OS_TiO2_HCD_OT_rep1.novor.csv.gz) |
| [DeepNovo](https://github.com/nh2tran/DeepNovo) | mgf:txt |
| [PepNovo](http://proteomics.ucsd.edu/software-tools/531-2/) | mgf:txt |
| [pNovo+](http://pfind.ict.ac.cn/software/pNovo/) | mgf:txt |

#### Proteogenomics:


| Type        | Example files |
| ------------|---------------|
| [proBAM](https://genomebiology.biomedcentral.com/articles/10.1186/s13059-017-1377-x) | [ProBAM.tar.gz](http://pdv.zhang-lab.org/data/download/upload/ProBAM.tar.gz) |
| [proBed](https://genomebiology.biomedcentral.com/articles/10.1186/s13059-017-1377-x) | [ProBed.tar.gz](http://pdv.zhang-lab.org/data/download/upload/ProBed.tar.gz) |

#### One PSM:


#### Spectrum library:

#### MS data:

| Type   |Example files|
| -------|-------------|
| mzML | [SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzML.gz) |
| mzXML|[SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz](http://pdv.zhang-lab.org/data/download/test_data/msdata/SF_200217_U2OS_TiO2_HCD_OT_rep1.mzXML.gz) |

#### PRIDE XML:


#### QC analysis:

Please find an example in this tutorial: [QC analysis](http://bioconductor.org/packages/devel/bioc/vignettes/proteoQC/inst/doc/proteoQC.html). 


## Citation

To cite the `PDV` package in publications, please use:

Coming soon.

## List of citations

`PDV` has been cited in the following manuscripts:
1. Wang X, Codreanu S G, Wen B, et al. Detection of proteome diversity resulted from alternative splicing is limited by trypsin cleavage specificity. Molecular & Cellular Proteomics, 2017: mcp. RA117. 000155.
2. Menschaert G, Wang X, Jones A R, et al. The proBAM and proBed standard formats: enabling a seamless integration of genomics and proteomics data. Genome biology, 2018, 19(1): 12.
## Contribution

Contributions to the package are more than welcome. 
