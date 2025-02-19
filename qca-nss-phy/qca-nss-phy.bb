DESCRIPTION = "NSS PHY Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-phy \
	  "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-nss-phy"

EXTRA_CFLAGS +="-I${STAGING_INCDIR}"

SUPPORTED_PHYS = "CONFIG_NSSPHY_QCA808X CONFIG_NSSPHY_QCA81XX CONFIG_NSSPHY_QCA803X"
QCA_NSS_PHY_CONFIG_OPTS:ipq95xx += "supported_phys="${SUPPORTED_PHYS} CONFIG_NSSPHY_QCA807X""
QCA_NSS_PHY_CONFIG_OPTS:ipq95xx_64 += "supported_phys="${SUPPORTED_PHYS} CONFIG_NSSPHY_QCA807X""
QCA_NSS_PHY_CONFIG_OPTS:ipq53xx += "supported_phys="${SUPPORTED_PHYS}""
QCA_NSS_PHY_CONFIG_OPTS:ipq53xx_64 += "supported_phys="${SUPPORTED_PHYS}""
QCA_NSS_PHY_CONFIG_OPTS:ipq54xx += "supported_phys="${SUPPORTED_PHYS}""
QCA_NSS_PHY_CONFIG_OPTS:ipq54xx_64 += "supported_phys="${SUPPORTED_PHYS}""

QCA_NSS_PHY_CONFIG_OPTS+= "TOOL_PATH=${STAGING_BINDIR_TOOLCHAIN} \
                         SYS_PATH=${STAGING_KERNEL_BUILDDIR} \
                         TOOLPREFIX=${TARGET_PREFIX} \
                         KVER=${KERNEL_VERSION} \
			 EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
                         ARCH=${KARCH} \
                         OS='linux' "

QCA_NSS_PHY_CONFIG_OPTS:append = "${@' ' if d.getVar('CONFIG_KERNEL_IPQ_MEM_PROFILE', True) == '256' or d.getVar('CONFIG_LOWMEM_FLASH', True) == 'y' else ' nss-phy-ptp=y'}"

do_compile() {
	${MAKE} -C ${STAGING_KERNEL_BUILDDIR} ${QCA_NSS_PHY_CONFIG_OPTS} M=${S} modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/nss_ext/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/linux_std/qca81xx/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/linux_std/qca8084/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/linux_std/ptp/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-phy
	install -m 0644 ${S}/nss_ext/*.h ${D}${includedir}/qca-nss-phy/

	#Managing the Load Order of Kernel Modules
	install -d ${D}${sysconfdir}/modprobe.d

	MODULES="qca8084-phy qca81xx-phy"
	MODPROBE_CMD=""

	for module in ${MODULES}; do
		MODPROBE_CMD="${MODPROBE_CMD} /sbin/modprobe ${module};"
	done
	MODPROBE_CMD="${MODPROBE_CMD} /sbin/modprobe --ignore-install qca-nss-phy"
	echo "install qca-nss-phy ${MODPROBE_CMD}" > ${D}${sysconfdir}/modprobe.d/qca-nss-phy.conf

	echo "softdep qca8xxx-phc pre: qca-ssdk" > ${D}${sysconfdir}/modprobe.d/qca8xxx-phc.conf
}

KERNEL_MODULE_AUTOLOAD += "qca8084-phy"
KERNEL_MODULE_AUTOLOAD += "qca81xx-phy"
KERNEL_MODULE_AUTOLOAD += "qca-nss-phy"
KERNEL_MODULE_AUTOLOAD += "qca8xxx-phc"
