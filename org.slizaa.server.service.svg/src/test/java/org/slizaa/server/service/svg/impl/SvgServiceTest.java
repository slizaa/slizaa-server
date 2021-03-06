package org.slizaa.server.service.svg.impl;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slizaa.server.service.backend.IBackendService;
import org.slizaa.server.service.configuration.IConfigurationService;
import org.slizaa.server.service.svg.ISvgService;
import org.slizaa.server.service.svg.impl.fwk.SvgServiceTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SvgServiceTestConfiguration.class)
public class SvgServiceTest {

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();

  @BeforeClass
  public static void before() throws Exception {
    System.setProperty("configuration.rootDirectory", folder.newFolder().getAbsolutePath());
  }

  @Autowired
  private IConfigurationService _configurationService;

  @Autowired
  private IBackendService       _backendService;

  @Autowired
  private ISvgService           _svgService;

  @Test
  public void test() throws Exception {

//    String key = ImageKey.key("bumm", "bamm", "bemm", null, "hurz");
//
//    String shortendKey = _svgService.getOrCreateKey("bumm", "bamm", "bemm", null, "hurz");
//
//    Map<String, String> map = _configurationService.load(SvgServiceImpl.CONFIGURATION_ID, HashMap.class);
//    assertThat(map).containsKey(key);
//    assertThat(map.get(key)).isEqualTo(shortendKey);

    //
    OverlaySvgIcon overlaySvgIcon = new OverlaySvgIcon(true);

    //
    Document document = XMLWriterDOM.read(_backendService.loadResourceFromExtensions("icons/class_obj.svg"));
    overlaySvgIcon.setMainNodes(document.getDocumentElement().getChildNodes());

    document = XMLWriterDOM.read(_backendService.loadResourceFromExtensions("icons/abstract_ovr.svg"));
    overlaySvgIcon.setUpperRightNodes(document.getDocumentElement().getChildNodes());

    document = XMLWriterDOM.read(_backendService.loadResourceFromExtensions("icons/private_ovr.svg"));
    overlaySvgIcon.setUpperLeftNodes(document.getDocumentElement().getChildNodes());

    System.out.println();
    System.out.println(overlaySvgIcon.create());
    System.out.println();

//    for (int i = 0; i < nodeList.getLength(); i++) {
//      
//      Node node = nodeList.item(i);
//      if (node.getNodeType() == Node.ELEMENT_NODE) {
//        System.out.println("------------------");
//        XMLWriterDOM.dump(nodeList.item(i));
//      }
//    }
  }
}
