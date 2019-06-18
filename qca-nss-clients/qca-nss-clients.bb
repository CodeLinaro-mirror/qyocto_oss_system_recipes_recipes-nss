DESCRIPTION = "NSS Clients"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
include ${THISDIR}/files/qca-nss-clients.inc

SOC_TYPE="${@bb.data.getVar('SOC_FAMILY', d, 1).split(':')[1]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-clients \
	  "

DEPENDS = "virtual/kernel qca-nss-drv qca-ssdk-nohnat qca-nss-crypto"
DEPENDS_append_qca-nss-drv-ipsecmgr += "qca-nss-cfi"
DEPENDS_append_qca-nss-drv-dtlsmgr += "qca-nss-cfi"
DEPENDS_append_qca-nss-drv-map-t += "nat46"

RDEPENDS-${PN} += "qca-nss-drv"
RDEPENDS-qca-nss-drv-tun6rd += "sit"
RDEPENDS-qca-nss-drv-dtlsmgr += "qca-nss-cfi"
RDEPENDS-qca-nss-drv-l2tpv2 += "ppp l2tp"
RDEPENDS-qca-nss-drv-pptp += "pptp"
RDEPENDS-qca-nss-drv-pppoe += "pppoe bonding"
RDEPENDS-qca-nss-drv-map-t += "nat46"
RDEPENDS-qca-nss-drv-tunipip6 += "iptunnel6 ip6-tunnel"
RDEPENDS-qca-nss-drv-gre += "gre6"
RDEPENDS-qca-nss-drv-ipsecmgr += "qca-nss-cfi-cryptoapi"
RDEPENDS-qca-nss-drv-capwapmgr += "qca-nss-drv-dtlsmgr"
RDEPENDS-qca-nss-drv-bridge-mgr += "bonding qca-nss-drv-vlan-mgr"
RDEPENDS-qca-nss-drv-lag-mgr += "bonding qca-nss-drv-vlan-mgr"

S = "${WORKDIR}/qca-nss-clients"

CLIENT_MODULES = ""
CLIENT_MODULES_append_qca-nss-drv-profile += "profile=y"
CLIENT_MODULES_append_qca-nss-drv-capwapmgr += "capwapmgr=y"
CLIENT_MODULES_append_qca-nss-drv-tun6rd += "tun6rd=m"
CLIENT_MODULES_append_qca-nss-drv-dtlsmgr += "dtlsmgr=y"
CLIENT_MODULES_append_qca-nss-drv-l2tpv2 += "l2tpv2=y"
CLIENT_MODULES_append_qca-nss-drv-pptp += "pptp=y"
CLIENT_MODULES_append_qca-nss-drv-pppoe += "pppoe=y"
CLIENT_MODULES_append_qca-nss-drv-map-t += "map-t=y"
CLIENT_MODULES_append_qca-nss-drv-tunipip6 += "tunipip6=m"
CLIENT_MODULES_append_qca-nss-drv-qdisc += "qdisc=y"
CLIENT_MODULES_append_qca-nss-drv-ipsecmgr += "ipsecmgr=y"
CLIENT_MODULES_append_qca-nss-drv-bridge-mgr += "bridge-mgr=y"
CLIENT_MODULES_append_qca-nss-drv-vlan-mgr += "vlan-mgr=y"
CLIENT_MODULES_append_qca-nss-drv-lag-mgr += "lag-mgr=y"
CLIENT_MODULES_append_qca-nss-drv-gre += "gre=y"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-drv \
		-I${STAGING_INCDIR}/qca-nss-crypto \
		-I${STAGING_INCDIR}/qca-nss-gmac \
		-I${STAGING_INCDIR}/qca-nss-cfi \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/nat46 \
		"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${CLIENT_MODULES} \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		ARCH='${KARCH}' \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		SoC='${SOC_TYPE}' \
		DTLSMGR_DIR="v2.0" \
		IPSECMGR_DIR="v2.0" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	rm -rf ${STAGING_INCDIR}/qca-nss-clients
	install -d ${D}${includedir}/qca-nss-clients
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-clients/.
}

do_install_append_qca-nss-drv-profile() {
	install -m 0644 ${S}/profiler/qca-nss-profile-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-capwapmgr() {
	install -m 0644 ${S}/capwapmgr/qca-nss-capwapmgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-tun6rd() {
	install -m 0644 ${S}/qca-nss-tun6rd${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-dtlsmgr() {
	install -m 0644 ${S}/dtls/v2.0/qca-nss-dtlsmgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-l2tpv2() {
	install -m 0644 ${S}/l2tp/l2tpv2/qca-nss-l2tpv2${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-pptp() {
	install -m 0644 ${S}/pptp/qca-nss-pptp${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-pppoe() {
	install -m 0644 ${S}/pppoe/qca-nss-pppoe${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-map-t() {
	install -m 0644 ${S}/map/map-t/qca-nss-map-t${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-tunipip6() {
	install -m 0644 ${S}/qca-nss-tunipip6${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-qdisc() {
	install -m 0644 ${S}/nss_qdisc/qca-nss-qdisc${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-ipsecmgr() {
	install -m 0644 ${S}/ipsecmgr/v2.0/qca-nss-ipsecmgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-bridge-mgr() {
	install -m 0644 ${S}/bridge/qca-nss-bridge-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-vlan-mgr() {
	install -m 0644 ${S}/vlan/qca-nss-vlan${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-lag-mgr() {
	install -m 0644 ${S}/lag/qca-nss-lag-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

do_install_append_qca-nss-drv-gre() {
	install -m 0644 ${S}/gre/qca-nss-gre${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
	install -m 0644 ${S}/gre/test/qca-nss-gre-test${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}/.
}

FILES_${PN}-dev = "${includedir}/qca-nss-clients"
INSANE_SKIP_${PN} = "dev"

KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-profile += "qca-nss-profile-drv"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-capwapmgr += "qca-nss-capwapmgr"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-tun6rd += "qca-nss-tun6rd"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-dtlsmgr += "qca-nss-dtlsmgr"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-l2tpv2 += "qca-nss-l2tpv2"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-pptp += "qca-nss-pptp"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-pppoe += "qca-nss-pppoe"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-map-t += "qca-nss-map-t"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-tunipip6 += "qca-nss-tunipip6"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-qdisc += "qca-nss-qdisc"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-ipsecmgr += "qca-nss-ipsecmgr"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-bridge-mgr += "qca-nss-bridge-mgr"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-vlan-mgr += "qca-nss-vlan"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-lag-mgr += "qca-nss-lag-mgr"
KERNEL_MODULE_AUTOLOAD_append_qca-nss-drv-gre += "qca-nss-gre \
						qca-nss-gre-test \
						"
