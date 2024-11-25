DESCRIPTION = "AQR PHY driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"

inherit module

FILESEXTRAPATHS:prepend := "${THISDIR}/:"
SRCREV = "5e182fbacb112c7adc366621d0097f89247d4265"
SRC_URI = "git://github.com/Aquantia/linux-aqr-phy-only.git;protocol=https;branch=main \
file://src"

DEPENDS = "virtual/kernel"

AQR_PHY_MAKE_OPTS = "\
	CROSS=${TARGET_PREFIX} \
	INSTALL_ROOT=${WORKDIR}/install \
	"
do_compile() {
	SUBDIR="git/aquantia"
	cp ${WORKDIR}/src/Makefile ${WORKDIR}/${SUBDIR}
	oe_runmake -C ${STAGING_KERNEL_BUILDDIR} ${AQR_PHY_MAKE_OPTS} M=${WORKDIR}/${SUBDIR} modules
}

do_install() {
	if [ !$(CONFIG_AQUANTIA_PHY) ]; then
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${WORKDIR}/git/aquantia/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${sysconfdir}/modprobe.d
	echo "softdep aqr_phy post: qca-nss-phy" > ${D}${sysconfdir}/modprobe.d/aqr-phy.conf
	fi
}

KERNEL_MODULE_AUTOLOAD += "aqr-phy"
